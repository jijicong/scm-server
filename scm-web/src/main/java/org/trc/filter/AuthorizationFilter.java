package org.trc.filter;

import com.alibaba.fastjson.JSONObject;
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
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.exception.CategoryException;
import org.trc.service.category.IBrandService;
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

    private Logger  log = LoggerFactory.getLogger(AuthorizationFilter.class);
//    @Value("${app.id}")
//    private String appId;
//    @Value("${app.key}")
//    private String appKey;
//
//    @Resource
//    private BeegoService beegoService;
//    @Autowired
//    private JurisdictionBiz jurisdictionBiz;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
//        URI url = ((ContainerRequest) requestContext).getRequestUri();
//        String method = ((ContainerRequest) requestContext).getMethod();
//        String token =_getToken(requestContext);
//        if (StringUtils.isNotBlank(token)) {
//            BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(
//                    appId,
//                    appKey,
//                    token);
//
//            BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
//            if (null != beegoToken) {
//                String userId = beegoToken.getUserId();
//                try {
//                    if (!jurisdictionBiz.authCheck(userId, url.toString(), method)) {
//                        AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "用户无此权限", null);
//                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
//                    }
//                } catch (CategoryException e) {
//                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), e.getMessage(), null);
//                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
//                } catch (Exception e) {
//                    AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ExceptionEnum.SYSTEM_BUSY.getMessage(), null);
//                    requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
//                }
//            }
//        } else {
//            //未获取到token返回登录页面
//            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(),"用户未登录", null);
//            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
//        }
    }

    private String _getToken(ContainerRequestContext requestContext) {
        String token = null;
        Cookie cookie = requestContext.getCookies().get("token");
        if (cookie != null) {
            token = cookie.getValue();
        }
        return token;
    }

}
