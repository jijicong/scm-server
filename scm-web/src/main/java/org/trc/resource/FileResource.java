package org.trc.resource;

import com.alibaba.fastjson.JSONObject;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;

/**
 * Created by hzwdx on 2017/5/2.
 */
@Component
@Path("file")
public class FileResource {




    /**
     * Constants operating with images
     */
    private static final String ARTICLE_IMAGES_PATH = "c:/Newsportal/article_images/";
    private static final String JPG_CONTENT_TYPE = "image/jpeg";
    private static final String PNG_CONTENT_TYPE = "image/png";

    /**
     * 第一种方式上传
     *
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadimage1(@FormDataParam("Filedata") InputStream fileInputStream,
                               @FormDataParam("Filedata") FormDataContentDisposition disposition) {
        String imageName = Calendar.getInstance().getTimeInMillis()
                + disposition.getFileName();
        File file = new File(ARTICLE_IMAGES_PATH + imageName);
        try {
            //使用common io的文件写入操作
            FileUtils.copyInputStreamToFile(fileInputStream, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "images/" + imageName;
    }






}
