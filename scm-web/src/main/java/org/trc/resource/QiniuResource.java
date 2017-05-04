package org.trc.resource;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.IQinniuBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.QinniuForm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Path(SupplyConstants.QinNiu.ROOT)
public class QiniuResource {

    @Autowired
    private IQinniuBiz qinniuBiz;
/*
    @GET
    @Path(SupplyConstants.QinNiu.TEST_TOKEN)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject testToken(@CookieParam("token") String token) {
        String qiniuUpToken = qinniuForm.getAuth().uploadToken(qinniuForm.getBucket());
        JSONObject json = new JSONObject();
        json.put("uptoken", qiniuUpToken);
        json.put("prefix", "http://7xjivo.com2.z0.glb.qiniucdn.com");
        return json;
    }

    @GET
    @Path(SupplyConstants.QinNiu.PRODUCT_TOKEN)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject productToken(@CookieParam("token") String token) {
        String qiniuUpToken = qinniuForm.getAuth().uploadToken(qinniuForm.getBucket());
        JSONObject json = new JSONObject();
        json.put("uptoken", qiniuUpToken);
        json.put("prefix", "http://7xlpa2.com2.z0.glb.qiniucdn.com");
        return json;
    }*/

    /**
     * 文件上传
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String upload(@FormDataParam("Filedata") InputStream fileInputStream,
                               @FormDataParam("Filedata") FormDataContentDisposition disposition) throws Exception {
        String imageName = Calendar.getInstance().getTimeInMillis()
                + disposition.getFileName();
        DefaultPutRet defaultPutRet = qinniuBiz.upload(fileInputStream, "goods/mypict.jpg");
        return defaultPutRet.key;
    }

    @GET
    @Path("download")
    @Consumes(MediaType.TEXT_PLAIN)
    public String upload() throws Exception {
       return qinniuBiz.download("goods/mypict.jpg");
    }

    /**
     * 缩略图
     * @return
     * @throws Exception
     */
    @GET
    @Path("thumbnail")
    @Consumes(MediaType.TEXT_PLAIN)
    public String thumbnail(@QueryParam("fileName") String fileName, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws Exception {
        return qinniuBiz.getThumbnail(fileName, width, height);
    }





}
