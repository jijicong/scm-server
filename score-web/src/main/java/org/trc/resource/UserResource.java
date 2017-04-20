package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.service.impl.AuthService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Component
public class UserResource {

    @Autowired
    private AuthService authService;

//    @Autowired
//    private BeegoService beegoService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserById(@Context ContainerRequestContext requestContext, @PathParam("id") Long id) {
        System.out.println(requestContext.getProperty("userId"));
        return "hello world:"+id;
    }

}
