package org.trc.application;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.trc.filter.AuthorizationFilter;
import org.trc.filter.ScoreRequestContextFilter;

/**
 * Created by george on 2017/4/6.
 */
public class ScoreApplication extends ResourceConfig {

    public ScoreApplication(){
        register(AuthorizationFilter.class);
        register(ScoreRequestContextFilter.class);
        register(JacksonJsonProvider.class);

        register(MultiPartFeature.class);

        packages("org.trc.resource");
        packages("io.swagger.jaxrs.listing");
        
    }

}
