package org.trc.filter;

import com.alibaba.fastjson.JSONObject;
import com.tairanchina.beego.api.exception.AuthenticateException;
import com.tairanchina.beego.api.model.BeegoToken;
import com.tairanchina.beego.api.model.BeegoTokenAuthenticationRequest;
import com.tairanchina.beego.api.service.BeegoService;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.impower.JurisdictionBiz;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.JurisdictionException;
import org.trc.service.category.IBrandService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.AppResult;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 2017/4/13.
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
    private JurisdictionBiz jurisdictionBiz;
    @Autowired
    private IUserAccreditInfoService userAccreditInfoService;
    //放行url
    private final static String PASS_URL_SELECT = "/select/";
    private final static String PASS_URL_DICT = "config/dicts";

    //1.判断该url是否需要进行拦截，不需要拦截直接放行
    //2.需要拦截的url判断用户是否登录，登录token是否过期，用户是否被停用
    //3.对url进行权限验证
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String url = ((ContainerRequest) requestContext).getPath(true);
        //那些不需要拦截的公共查询方法直接放行
        if (url.contains(PASS_URL_SELECT) || url.equals(PASS_URL_DICT)) {
            handlePassUrl(requestContext,url);
        }
        if (jurisdictionBiz.urlCheck(url)) {
            //说明此url需要被拦截
            String method = ((ContainerRequest) requestContext).getMethod();
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(appId, appKey, token);
                try {
                    BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
                    if (null != beegoToken) {
                        String userId = beegoToken.getUserId();
                        UserAccreditInfo userAccreditInfo = userAccreditInfoService.selectOneById(userId);
                        if (userAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.USER_BE_FORBIDDEN.getMessage(), null);
                            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                        }else{
                            requestContext.setProperty("userId", userId);
                            requestContext.setProperty("userAccreditInfo", userAccreditInfo);
                            if (!jurisdictionBiz.authCheck(userId, url, method)) {
                                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户无此权限", null);
                                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                            }
                        }
                    }
                } catch (AuthenticateException e) {
                    //token失效需要用户重新登录
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                }
            } else {
                //未获取到token返回登录页面
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", null);
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

    private void handlePassUrl(ContainerRequestContext requestContext,String url) {
        //那些不需要拦截的公共查询方法直接放行
        if (url.contains(PASS_URL_SELECT) || url.equals(PASS_URL_DICT)) {
            String token = _getToken(requestContext);
            if (StringUtils.isNotBlank(token)) {
                BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(appId, appKey, token);
                try {
                    BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
                    if (null != beegoToken) {
                        String userId = beegoToken.getUserId();
                        UserAccreditInfo userAccreditInfo = userAccreditInfoService.selectOneById(userId);
                        if (userAccreditInfo == null) {
                            //说明用户已经被禁用或者失效需要将用户退出要求重新登录或者联系管理员处理问题
                            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.USER_BE_FORBIDDEN.getMessage(), null);
                            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                        }else{
                            requestContext.setProperty("userId", userId);
                            requestContext.setProperty("userAccreditInfo", userAccreditInfo);
                        }
                    }
                } catch (AuthenticateException e) {
                    //token失效需要用户重新登录
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
                }
            } else {
                //未获取到token返回登录页面
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户未登录", null);
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
            }
        }
    }
}
