package org.trc.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.trc.form.DictForm;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * Created by hzwdx on 2017/4/18.
 */
@Path("test")
@Component
public class TestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object defualt(){
        JSONObject json = new JSONObject();
        json.put("key1",1);
        json.put("key2",2);
        return json;
    }

    @POST
    @Path("/{rrrrrr}")
    public String test1(@PathParam("rrrrrr") String rrrrrr){
        return "@@@@"+rrrrrr;
    }

    @GET
    @Path("/req/{p1}/{p2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object get(@Context UriInfo uriInfo, @PathParam("p1") String param1, @PathParam("p2") String param2, @QueryParam("name") String name, @BeanParam DictForm dictForm) {


        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        MultivaluedMap<String, String> pathParams = uriInfo.getPathParameters();
        System.out.println(queryParams.get("name"));
        System.out.println(pathParams.get("p1")+"     "+pathParams.get("p2"));

        System.out.println(param1+"    "+param2+"    "+name);

        System.out.println(JSON.toJSON(dictForm));

        return dictForm;
    }

}
