package org.trc.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.form.QinniuForm;
import org.trc.service.IQinniuService;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Service("qinniuService")
public class QinniuService implements IQinniuService{

    @Autowired
    private QinniuForm qinniuForm;

    @Override
    public Auth getAuth() throws Exception{
        return Auth.create(qinniuForm.getAccessKey(), qinniuForm.getSecretKey());
    }

    @Override
    public String getToken() throws Exception {
        return getAuth().uploadToken(qinniuForm.getBucket());
    }

    @Override
    public DefaultPutRet upload(InputStream inputStream, String fileName) throws Exception {
        Response response = getUploadManager().put(inputStream,fileName,getToken(),null, null);
        return new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
    }

    @Override
    public String download(String fileName) throws Exception {
        /*String domainOfBucket = qinniuForm.getDomainOfBucket();
        String encodedFileName = URLEncoder.encode(fileName, "utf-8");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);*/
        String url = getUrl(fileName,null,null);
        return getAuth().privateDownloadUrl(url);
    }

    @Override
    public String getThumbnail(String fileName, int width, int height) throws Exception {
        String url = getUrl(fileName,width, height);
        return getAuth().privateDownloadUrl(url);
    }

    @Override
    public List<String> getThumbnails(String fileNames, int width, int height) throws Exception {
        return null;
    }

    /**
     * 获取文件url
     * @param fileName 文件名称
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @return
     */
    private String getUrl(String fileName, Integer width, Integer height) throws Exception {
        String url = "";
        String domainOfBucket = qinniuForm.getDomainOfBucket();
        String encodedFileName = URLEncoder.encode(fileName, "utf-8");
        if(null == width || null == height){
            url = String.format("%s/%s", domainOfBucket, encodedFileName);
        }else {
            String imageView2Mode = String.format("imageView2/1/w/%s/h/%s", width, height);
            url = String.format("%s/%s?%s", domainOfBucket, encodedFileName, imageView2Mode);
        }
        return url;
    }

    /**
     * 获取上传管理类
     * @return
     */
    private UploadManager getUploadManager(){
        Configuration cfg = new Configuration(Zone.zone0());
        return new UploadManager(cfg);
    }

}
