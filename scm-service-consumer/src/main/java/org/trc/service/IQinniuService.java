package org.trc.service;

import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.trc.config.BaseThumbnailSize;
import org.trc.form.FileUrl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/3.
 */
public interface IQinniuService {
    /**
     * 获取七牛授权对象
     * @return
     */
    public Auth getAuth() throws Exception;

    /**
     * 获取token
     * @param stringMap 持久化参数map，可为空
     * @return
     * @throws Exception
     */
    public String getToken(StringMap stringMap) throws Exception;

    /**
     * 上传
     * @param inputStream 文件流
     * @param fileName 文件名称
     * @param baseThumbnailSize 缩略图尺寸配置对象
     * @return
     * @throws Exception
     */
    public DefaultPutRet upload(InputStream inputStream, String fileName, BaseThumbnailSize baseThumbnailSize) throws Exception;

    /**
     * 下载
     * @param fileName
     * @return
     */
    public String download(String fileName) throws Exception;

    /**
     * 获取缩略图地址
     * @param fileName 文件名称
     * @param width 缩略图的宽
     * @param height 缩略图的高
     * @return
     * @throws Exception
     */
    public String getThumbnail(String fileName, Integer width, Integer height) throws Exception;

    /**
     * 批量获取文件信息
     * @param fileNames
     * @return
     */
    public List<BatchStatus> batchGetFileInfo(String[] fileNames) throws Exception;

    /**
     * 批量获取文件url
     * @param fileNames 文件名称，多个用逗号分隔
     * @return
     */
    public List<FileUrl> batchGetFileUrl(String[] fileNames) throws Exception;

    /**
     * 批量删除
     * @param fileNames
     * @return map,{success:成功数,fialure:失败数,msg:错误信息}
     * @throws Exception
     */
    public Map<String, Object> batchDelete(String[] fileNames, BaseThumbnailSize baseThumbnailSize) throws Exception;

}
