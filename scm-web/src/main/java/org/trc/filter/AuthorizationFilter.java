package org.trc.filter;

import com.tairanchina.beego.api.model.BeegoToken;
import com.tairanchina.beego.api.model.BeegoTokenAuthenticationRequest;
import com.tairanchina.beego.api.service.BeegoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;

/**
 * Created by george on 2017/4/13.
 */
@Component
public class AuthorizationFilter implements ContainerRequestFilter {

    private final static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    /*@Value("${app.id}")
    private String appId = "62AA8318264C4875B449F57881487269";
    @Value("${app.key}")
    private String appKey = "$2a$10$GVYvws0vYpXBzSGXlxcu4OnSR9efqymhaCH7Txwl0pky5mBzSCHfi";

    @Resource
    private BeegoService beegoService;*/

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = _getToken(requestContext);
        if(StringUtils.isNotBlank(token)){
            /*BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(
                    appId,
                    appKey,
                    token);
            BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
            if(null != beegoToken){
                String userId = beegoToken.getUserId();
                requestContext.setProperty("userId", userId);
            }*/
        }else{
            requestContext.setProperty("userId", "-----");
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


}
