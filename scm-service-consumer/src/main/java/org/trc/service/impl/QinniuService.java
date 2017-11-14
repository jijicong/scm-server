package org.trc.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.config.BaseThumbnailSize;
import org.trc.config.ThumbnailSize;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.FileUrl;
import org.trc.form.QinniuForm;
import org.trc.service.IQinniuService;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Service("qinniuService")
public class QinniuService implements IQinniuService {
    private Logger log = LoggerFactory.getLogger(QinniuService.class);

    //url地址和参数分隔符号
    public static final String URL_PARAM_SPLIT = "?";
    //文件名称标志字符
    public static final String FILE_FLAG = ".";
    //七牛token失效期限,单位/s
    public static final long EXPIRE_DURATION = 3600;
    //

    @Autowired
    private QinniuForm qinniuForm;

    @Override
    public Auth getAuth() throws Exception {
        return Auth.create(qinniuForm.getAccessKey(), qinniuForm.getSecretKey());
    }

    @Override
    public String getToken(StringMap stringMap) throws Exception {
        return getAuth().uploadToken(qinniuForm.getBucket(), null, EXPIRE_DURATION, stringMap);
    }

    @Override
    public DefaultPutRet upload(InputStream inputStream, String fileName, BaseThumbnailSize baseThumbnailSize) throws Exception {
        StringMap stringMap = null;
        if (null != baseThumbnailSize) {
            stringMap = getPersistentOpfs(qinniuForm.getBucket(), fileName, baseThumbnailSize);
        }
        long startTime = System.nanoTime();
        Response response = getUploadManager().put(inputStream, fileName, getToken(stringMap), stringMap, null);
        long endTime = System.nanoTime();
        if(log.isInfoEnabled()){
            log.info(String.format("调用七牛上传接口put上传文件%s耗时%s毫秒", fileName, DateUtils.getMilliSecondBetween(startTime, endTime)));
        }
        return new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
    }

    @Override
    public String download(String fileName) throws Exception {
        String url = getUrl(fileName, null, null);
        long startTime = System.nanoTime();
        String _url = getAuth().privateDownloadUrl(url);
        long endTime = System.nanoTime();
        if(log.isInfoEnabled()){
            log.info(String.format("调用七牛privateDownloadUrl接口获取文件%s url地址耗时%s毫秒", fileName, DateUtils.getMilliSecondBetween(startTime, endTime)));
        }
        return _url;
    }

    @Override
    public String getThumbnail(String fileName, Integer width, Integer height) throws Exception {
        String url = getUrl(fileName, width, height);
        long startTime = System.nanoTime();
        String _url = getAuth().privateDownloadUrl(url);
        long endTime = System.nanoTime();
        if(log.isInfoEnabled()){
            log.info(String.format("调用七牛privateDownloadUrl接口获取文件%s url地址耗时%s毫秒", fileName, DateUtils.getMilliSecondBetween(startTime, endTime)));
        }
        return _url;
    }

    @Override
    public List<BatchStatus> batchGetFileInfo(String[] fileNames) throws Exception {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addStatOps(qinniuForm.getBucket(), fileNames);
        long startTime = System.nanoTime();
        Response response = getBucketManager().batch(batchOperations);
        long endTime = System.nanoTime();
        if(log.isInfoEnabled()){
            log.info(String.format("调用七牛批量获取接口获取文件%s信息耗时%s毫秒", CommonUtil.converCollectionToString(Arrays.asList(fileNames)), DateUtils.getMilliSecondBetween(startTime, endTime)));
        }
        BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
        return Arrays.asList(batchStatusList);
    }

    @Override
    public List<FileUrl> batchGetFileUrl(String[] fileNames) throws Exception {
        List<FileUrl> fileUrls = new ArrayList<FileUrl>();
        String domainOfBucket = qinniuForm.getDomainOfBucket();
        String firstFileUrl = download(fileNames[0]);
        String[] firstFileUrlSplit = firstFileUrl.split("\\" + URL_PARAM_SPLIT);
        String tokenParam = firstFileUrlSplit[1];
        for (String fileName : fileNames) {
            String encodedFileName = URLEncoder.encode(fileName, "utf-8");
            FileUrl fileUrl = new FileUrl();
            fileUrl.setFileKey(fileName);
            fileUrl.setUrl(String.format("%s/%s%s%s", domainOfBucket, encodedFileName, URL_PARAM_SPLIT, tokenParam));
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    @Override
    public Map<String, Object> batchDelete(String[] fileNames, BaseThumbnailSize baseThumbnailSize) throws Exception {
        String[] newFileNames = getDeleteFileNames(fileNames, baseThumbnailSize);
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(qinniuForm.getBucket(), newFileNames);
        Response response = getBucketManager().batch(batchOperations);
        BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
        StringBuilder builder = new StringBuilder();
        int success = 0;
        int fialure = 0;
        for (int i = 0; i < fileNames.length; i++) {
            BatchStatus status = batchStatusList[i];
            if (status.code == 200) {
                success++;
                System.out.println("delete success");
            } else {
                fialure++;
                builder.append("文件").append(fileNames[i]).append("删除失败，错误信息：").append(status.data.error).append(";");
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", success);
        map.put("fialure", fialure);
        map.put("msg", builder.toString());
        return map;
    }

    @Override
    public FetchRet fetch(String url, String fileName) throws Exception {
        return getBucketManager().fetch(url, fileName);
    }

    private String[] getDeleteFileNames(String[] fileNames, BaseThumbnailSize baseThumbnailSize) {
        List<String> fileList = new ArrayList<String>();
        if (null != baseThumbnailSize) {
            for (String fileName : fileNames) {
                fileList.add(fileName);
                String[] fileNameSplit = fileName.split("\\" + FILE_FLAG);
                for (ThumbnailSize size : baseThumbnailSize.getThumbnailSizes()) {
                    if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), size.getIsValid())) {
                        //缩略图名称
                        String thumbnailName = String.format("%s_%s_%s%s%s", fileNameSplit[0], size.getWidth(), size.getHeight(), FILE_FLAG, fileNameSplit[1]);
                        fileList.add(thumbnailName);
                    }
                }
            }
            return fileList.toArray(new String[fileList.size()]);
        } else {
            return fileNames;
        }
    }

    /**
     * 获取文件url
     *
     * @param fileName 文件名称
     * @param width    缩略图宽度
     * @param height   缩略图高度
     * @return
     */
    private String getUrl(String fileName, Integer width, Integer height) throws Exception {
        String url = "";
        String domainOfBucket = qinniuForm.getDomainOfBucket();
        String encodedFileName = URLEncoder.encode(fileName, "utf-8");
        if (null == width || null == height) {
            url = String.format("%s/%s", domainOfBucket, encodedFileName);
        } else {
            String imageView2Mode = String.format("imageView2/1/w/%s/h/%s", width, height);
            url = String.format("%s/%s?%s", domainOfBucket, encodedFileName, imageView2Mode);
        }
        return url;
    }

    /**
     * 获取上传文图片持久化参数
     *
     * @return
     */
    private StringMap getPersistentOpfs(String bucket, String fileName, BaseThumbnailSize baseThumbnailSize) {
        StringMap putPolicy = new StringMap();
        String[] fileNames = fileName.split("\\" + FILE_FLAG);
        List<String> thumbnailCmds = new ArrayList<String>();
        for (ThumbnailSize size : baseThumbnailSize.getThumbnailSizes()) {
            if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), size.getIsValid())) {
                //缩略图名称
                String thumbnailName = String.format("%s_%s_%s%s%s", fileNames[0], size.getWidth(), size.getHeight(), FILE_FLAG, fileNames[1]);
                String thumbnail = String.format("%s:%s", bucket, thumbnailName);
                //添加生成缩略图指令
                thumbnailCmds.add(String.format("imageView2/1/w/%s/h/%s|saveas/%s", size.getWidth(), size.getHeight(), UrlSafeBase64.encodeToString(thumbnail)));
            }
        }
        //将多个数据处理指令拼接起来
        String persistentOpfs = StringUtils.join(thumbnailCmds.toArray(), ";");
        putPolicy.put("persistentOps", persistentOpfs);
        //数据处理队列名称，必填
        putPolicy.put("persistentPipeline", qinniuForm.getPersistentPipeline());
        //数据处理完成结果通知地址
        //putPolicy.put("persistentNotifyUrl", "http://api.example.com/qiniu/pfop/notify");
        return putPolicy;
    }

    /**
     * 获取配置对象
     *
     * @return
     */
    private Configuration getConfiguration() {
        return new Configuration(Zone.zone0());
    }

    /**
     * 获取上传管理类
     *
     * @return
     */
    private UploadManager getUploadManager() {
        return new UploadManager(getConfiguration());
    }

    private BucketManager getBucketManager() throws Exception {
        return new BucketManager(getAuth(), getConfiguration());
    }

}
