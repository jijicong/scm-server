package org.trc.filter;

import com.tairanchina.beego.api.exception.AuthenticateException;
import com.tairanchina.beego.api.model.BeegoToken;
import com.tairanchina.beego.api.model.BeegoTokenAuthenticationRequest;
import com.tairanchina.beego.api.service.BeegoService;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.impower.AclResourceBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author george
 * @date 2017/4/13
 */
@Component
public class AuthorizationFilter implements ContainerRequestFilter {

    private Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);
    @Value("${app.id}")
    private String appId;
    @Value("${app.key}")
    private String appKey;

    @Resource
    private BeegoService beegoService;
    @Autowired
    private AclResourceBiz jurisdictionBiz;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    //对外提供api路径
    private final static String PASS_API_URL = "api";
    //渠道访问路径
    private final static String  PASS_TAI_RAN_URL= "tairan";
    /**
     *查询业务线资源需要UserID
     *
     */
    private final static String  CHANNEL_QUERY= "api/jurisdictionUserChannel";
    private final static String  CHANNEL_CONFIRM= "api/confirmUser";


    @Context private HttpServletRequest request;
    @Context
    private  HttpServletResponse response;
    @Context
    private ContainerResponseContext responseContext;


    //1.判断该url是用户内部url还是api接口，api接口直接放行不验证
    //2.需要拦截的url判断用户是否登录，登录token是否过期，用户是否被停用
    //3.对url进行验证，如果在权限列表中，则需要验证，不在则直接放行401是,403是
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String url = ((ContainerRequest) requestContext).getPath(true);
        //"/api"开头的给外部调用的接口直接放行
        if (!url.startsWith(PASS_API_URL) && !url.startsWith(PASS_TAI_RAN_URL) &&!url.startsWith(SupplyConstants.Metadata.ROOT)) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(appId, appKey, token);
                try {
                    BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
                    AclUserAccreditInfo aclUserAccreditInfo =null;
                    if (null != beegoToken) {
                        String userId = beegoToken.getUserId();
                        String channelCode = _getCookieChannelCode(requestContext);
                        List<AclUserAccreditInfo> accreditInfoList = userAccreditInfoService.selectUserListByUserId(userId,channelCode);
                            if (!AssertUtil.collectionIsEmpty(accreditInfoList)){
                                for (AclUserAccreditInfo accreditInfo:accreditInfoList ) {
                                    if (StringUtils.equals(channelCode,accreditInfo.getChannelCode())){
                                        aclUserAccreditInfo = accreditInfo;
                                    }
                                }
                            }else {
                                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "查询用户信息为空,请尝试重新登录!", null);
                                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
//                                return;
                            }

                        if (aclUserAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            log.warn("用户授权信息不存在或用户已经被禁用!");
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.USER_BE_FORBIDDEN.getMessage(), null);
                            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                        } else {
                            requestContext.setProperty(SupplyConstants.Authorization.USER_ID, userId);
                            requestContext.setProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO, aclUserAccreditInfo);
                            String method = ((ContainerRequest) requestContext).getMethod();
                            //验证是否在需要验证的权限列表中，需要则验证，不需要url直接放行
                            if (jurisdictionBiz.urlCheck(url)) {
                                //验证权限
                                if (!jurisdictionBiz.authCheck(userId, url, method)) {
                                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户无此权限", null);
                                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                                }
                            }else{
                                log.info("url:{}不需要验证放行成功",url);
                            }
                        }
                    }
                } catch (AuthenticateException e) {
                    //token失效需要用户重新登录
                    log.error("message:{},e:{}",e.getMessage(),e);
                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                } catch (Exception e) {
                    log.error("message:{},e:{}",e.getMessage(),e);
                    requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                }
            } else {
                //未获取到token返回登录页面
                log.info("页面token不存在,需要重新登录");
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
            }
        }
        /**
         * 获取userId用于查询当前用户下的业务线
         */
        if (url.startsWith(CHANNEL_QUERY)) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(appId, appKey, token);
                try {
                    BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
                    if (null != beegoToken) {
                        String userId = beegoToken.getUserId();
                        AclUserAccreditInfo aclUserAccreditInfo = userAccreditInfoService.selectOneById(userId);
                        if (aclUserAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            log.warn("用户授权信息不存在或用户已经被禁用!");
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.USER_BE_FORBIDDEN.getMessage(), null);
                            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                        } else {
                            requestContext.setProperty(SupplyConstants.Authorization.USER_ID, userId);
                            requestContext.setProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO, aclUserAccreditInfo);
                        }
                    }
                } catch (AuthenticateException e) {
                    //token失效需要用户重新登录
                    log.error("message:{},e:{}", e.getMessage(), e);
                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                } catch (Exception e) {
                    log.error("message:{},e:{}", e.getMessage(), e);
                    requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                }
            } else {
                //未获取到token返回登录页面
                log.info("页面token不存在,需要重新登录");
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
            }
        }
        //设置选中的业务线上下文
        if (url.startsWith(CHANNEL_CONFIRM)) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                String channelCode = _getChannelCode(requestContext);
                if (StringUtils.isNotBlank(channelCode)) {
                    //request.getHeaders().add("Set-Cookie", new NewCookie("channelCode", channelCode));
                    //request.getSession(true).setAttribute("channelCode", channelCode);
                } else {
                    //未获取到token返回登录页面
                    log.info("设置channelCode的值为空!");
                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "业务线未选择!", Response.Status.FORBIDDEN.getStatusCode());
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());

                }
            } else {
                //未获取到token返回登录页面
                log.info("页面token不存在,需要重新登录");
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
            }

        }
    }

    private String _getToken(ContainerRequestContext requestContext) {
        String token = null;
        Cookie cookie = requestContext.getCookies().get("token");
        if (cookie != null) {
            token = cookie.getValue();
        }
        return token;
    }


    private String _getCookieChannelCode(ContainerRequestContext requestContext) {

        HttpSession session = request.getSession(false);
        if(null == session){
            return StringUtils.EMPTY;
        }
        return session.getAttribute("channelCode").toString();

    }
    private String _getChannelCode(ContainerRequestContext requestContext) {
        String channelCodeUrl;
        String channelCodeString = "channelCode";
        int channelCodeLength = 2;
        String[] channelCode;
        channelCodeUrl= requestContext.getUriInfo().getRequestUri().getQuery();
        if (StringUtils.isNotBlank(channelCodeUrl)) {
            channelCode =StringUtils.split(channelCodeUrl,SupplyConstants.Symbol.EQUAL);
           if (channelCode.length==channelCodeLength){
               if (channelCodeString.equals(channelCode[0])){
                    return channelCode[1];
               }
           }
        }
        return "";
    }


}
