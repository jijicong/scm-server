package org.trc.resource;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.UploadResponse;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Component
@Path(SupplyConstants.QinNiu.ROOT)
public class QiniuResource {

    //逗号
    private static final String DOU_HAO = ",";
    //缩略图宽度
    private static final int WIDTH = 150;
    //缩略图高度
    private static final int HEIGHT = 150;

    @Autowired
    private IQinniuBiz qinniuBiz;

    /**
     * 文件上传
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Path(SupplyConstants.QinNiu.UPLOAD+"/{module}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult upload(@FormDataParam("Filedata") InputStream fileInputStream,
                            @FormDataParam("Filedata") FormDataContentDisposition disposition,
                            @PathParam("module") String module) throws Exception {
        String key = qinniuBiz.upload(fileInputStream, disposition.getFileName(), module);
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setKey(key);
        uploadResponse.setFileName(disposition.getFileName());
        //获取图片缩略图url
        String url = qinniuBiz.getThumbnail(key, WIDTH, HEIGHT);
        uploadResponse.setUrl(url);
        List<UploadResponse> list = new ArrayList<UploadResponse>();
        list.add(uploadResponse);
        return ResultUtil.createSucssAppResult("上传成功",list);
    }

    @GET
    @Path(SupplyConstants.QinNiu.DOWNLOAD)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult download(@QueryParam("fileName") String fileName) throws Exception {
       return ResultUtil.createSucssAppResult("下载成功", qinniuBiz.download(fileName));
    }

    /**
     * 缩略图
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.QinNiu.THUMBNAIL)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult thumbnail(@QueryParam("fileName") String fileName, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws Exception {
        return ResultUtil.createSucssAppResult("获取缩略图成功", qinniuBiz.getThumbnail(fileName, width, height));
    }

    /**
     * 批量获取多个文件的url
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.QinNiu.URLS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult urls(@QueryParam("fileNames") String fileNames, @QueryParam("thumbnail") String thumbnail) throws Exception {
        String[] fileNames2 = fileNames.split(DOU_HAO);
        return ResultUtil.createSucssAppResult("批量获取url成功",qinniuBiz.batchGetFileUrl(fileNames2, thumbnail));
    }

    /**
     * 批量获取多个文件的url
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.QinNiu.DELETE+"/{module}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult delete(@QueryParam("fileNames") String fileNames, @PathParam("module") String module) throws Exception {
        String[] fileNames2 = fileNames.split(DOU_HAO);
        return ResultUtil.createSucssAppResult("删除成功",qinniuBiz.batchDelete(fileNames2, module));
    }


}
