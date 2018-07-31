package org.trc.filter;

import com.tairanchina.beego.api.exception.AuthenticateException;
import com.tairanchina.csp.foundation.common.sdk.CommonConfig;
import com.tairanchina.csp.foundation.sdk.CSPKernelSDK;
import com.tairanchina.csp.foundation.sdk.dto.TokenDeliverDTO;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.impower.AclResourceBiz;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.enums.UserTypeEnum;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.CommonConfigUtil;

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

    @Value("${apply.id}")
    private String applyId;

    @Value("${apply.secret}")
    private String applySecret;

    @Value("${apply.uri}")
    private String applyUri;

    @Autowired
    private AclResourceBiz jurisdictionBiz;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private IAclUserAccreditInfoBiz aclUserAccreditInfoBiz;
    //对外提供api路径
    private final static String PASS_API_URL = "api";
    //渠道访问路径
    private final static String  PASS_TAI_RAN_URL= "tairan";
    /**
     * 奇门访问路径
     */
    private  final  static  String PASS_QIMEN = "Qimen";
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
        if (!url.startsWith(PASS_API_URL) && !url.startsWith(PASS_TAI_RAN_URL) &&!url.startsWith(SupplyConstants.Metadata.ROOT)&&!url.startsWith(SupplyConstants.Qimen.QI_MEN)) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                TokenDeliverDTO tokenInfo = getCSPKernelSDK(token,url);
                    AclUserAccreditInfo aclUserAccreditInfo =null;
                    if (null != tokenInfo) {
                        String userId = tokenInfo.getUserId();
                        String channelCode = _getCookieChannelCode(requestContext);
                        if (StringUtils.isBlank(channelCode)){
                            aclUserAccreditInfo =  userAccreditInfoService.selectOneById(userId);
                            if (null == aclUserAccreditInfo){
                                log.warn("用户授权信息不存在或用户已经被禁用!");
                                logOut(tokenInfo);
                            }else
                            if (!StringUtils.equals(aclUserAccreditInfo.getUserType(), UserTypeEnum.OVERALL_USER.getCode())){
                                aclUserAccreditInfo =null;
                            }
                        }
                        List<AclUserAccreditInfo> accreditInfoList = userAccreditInfoService.selectUserListByUserId(userId,channelCode);
                        if (!AssertUtil.collectionIsEmpty(accreditInfoList)) {
                            for (AclUserAccreditInfo accreditInfo : accreditInfoList) {
                                if (StringUtils.equals(channelCode, accreditInfo.getChannelCode())) {
                                    aclUserAccreditInfo = accreditInfo;
                                }
                            }
                        } else if (AssertUtil.collectionIsEmpty(accreditInfoList)&&null==aclUserAccreditInfo){
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "查询用户信息为空,请尝试重新登录!", null);
                            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                            return;
                        }

                        if (aclUserAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            log.warn("用户授权信息不存在或用户已经被禁用!");
                            logOut(tokenInfo);
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
                    } else {
                        log.info("获取tokenInfo失败,需要重新登录");
                        AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
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
        if (url.startsWith(CHANNEL_QUERY)||url.startsWith("api/clearSession")) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                TokenDeliverDTO tokenInfo = getCSPKernelSDK(token,url);
                    if (null != tokenInfo) {
                        String userId = tokenInfo.getUserId();
                        AclUserAccreditInfo aclUserAccreditInfo = userAccreditInfoService.selectOneById(userId);
                        if (aclUserAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            log.warn("用户授权信息不存在或用户已经被禁用!");
                            logOut(tokenInfo);
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.USER_BE_FORBIDDEN.getMessage(), null);
                            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                        } else {
                            requestContext.setProperty(SupplyConstants.Authorization.USER_ID, userId);
                            requestContext.setProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO, aclUserAccreditInfo);
                        }
                    } else {
                        log.info("获取tokenInfo失败,需要重新登录");
                        AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", Response.Status.FORBIDDEN.getStatusCode());
                        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
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
        if (null == session) {
            return StringUtils.EMPTY;
        } else if (null != session.getAttribute("channelCode")) {
            return session.getAttribute("channelCode").toString();
        }
        return StringUtils.EMPTY;

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

    private TokenDeliverDTO getCSPKernelSDK(String token,String url){
        CommonConfig config = new CommonConfig();
        CommonConfig.Basic basicConfig = config.getBasic();
        basicConfig.setUrl(applyUri);
        basicConfig.setAppId(applyId);
        basicConfig.setAppSecret(applySecret);
    	CSPKernelSDK sdk = CommonConfigUtil.getCSPKernelSDK(applyUri, applyId, applySecret);
        TokenDeliverDTO tokenInfo = sdk.user.tenantValidate(token, url, config).getBody();
        return tokenInfo;
    }
    private void logOut(TokenDeliverDTO tokenInfo){
        aclUserAccreditInfoBiz.logOut(tokenInfo.getUserId());
        request.getSession().removeAttribute("channelCode");
        request.getSession().invalidate();
    }
}
