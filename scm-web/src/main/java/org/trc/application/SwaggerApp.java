package org.trc.application;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.swagger.jaxrs.config.BeanConfig;
@Component
public class SwaggerApp {
	
	@Value("${admin.profile}")
	private String env;

	@Value("${admin.server.url}")
	private String url;
	
	private final static String ONLINE = "online";
	
	@PostConstruct
    public void init() {
		
		/**
		 * 排除正式环境
		 */
		if (!ONLINE.equalsIgnoreCase(env)) {
			
			BeanConfig beanConfig = new BeanConfig();
			beanConfig.setVersion("1.0.0");
			beanConfig.setSchemes(new String[]{"http"});
			beanConfig.setHost(url);
			beanConfig.setBasePath("/scm-web");
			beanConfig.setResourcePackage("org.trc.resource");
			beanConfig.setScan(true);
		}
    }
}
