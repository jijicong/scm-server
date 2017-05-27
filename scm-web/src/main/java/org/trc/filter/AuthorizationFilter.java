package org.trc.filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.trc.service.category.IBrandService;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * Created by george on 2017/4/13.
 */
@Component
public class AuthorizationFilter implements ContainerRequestFilter,ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);
    private static ApplicationContext applicationContext=null;
//    @Resource
//    private IBaseService   baseService;
//    @Value("${app.id}")
//    private String appId = "62AA8318264C4875B449F57881487269";
//    @Value("${app.key}")
//    private String appKey = "$2a$10$GVYvws0vYpXBzSGXlxcu4OnSR9efqymhaCH7Txwl0pky5mBzSCHfi";
//
//    @Resource
//    private BeegoService beegoService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uri=requestContext.getUriInfo();
        uri.getAbsolutePath();
        IBrandService brandService= (IBrandService) applicationContext.getBean("brandService");
        System.out.print(1);
        requestContext.getRequest();

//        String token = _getToken(requestContext);
//        if(StringUtils.isNotBlank(token)){
//            BeegoTokenAuthenticationRequest beegoAuthRequest = new BeegoTokenAuthenticationRequest(
//                    appId,
//                    appKey,
//                    token);
//            BeegoToken beegoToken = beegoService.authenticationBeegoToken(beegoAuthRequest);
//            if(null != beegoToken){
//                String userId = beegoToken.getUserId();
//                requestContext.setProperty("userId", userId);
//            }
//        }else{
//            requestContext.setProperty("userId", "-----");
//        }
//        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("").type(MediaType.APPLICATION_JSON).encoding("UTF-8").build());
    }

    private String _getToken(ContainerRequestContext requestContext) {
        String token = null;
        Cookie cookie = requestContext.getCookies().get("token");
        if (cookie != null) {
            token = cookie.getValue();
        }
        return token;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
