package org.trc.biz;

import com.qiniu.storage.model.DefaultPutRet;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/3.
 */
public interface IQinniuBiz {
    /**
     * 上传
     * @param inputStream 文件流
     * @param fileName 文件名称
     * @param module 系统模块
     * @return String 文件路径
     * @throws Exception
     */
    public String upload(InputStream inputStream, String fileName, String module) throws Exception;

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
    public String getThumbnail(String fileName, int width, int height) throws Exception;

    /**
     * 批量获取多个文件的url
     * @param fileNames 文件名称数组
     * @return
     * @throws Exception
     */
    public Map<String, String>  batchGetFileUrl(String[] fileNames) throws Exception;

}
