package org.trc.biz;

import com.qiniu.storage.model.DefaultPutRet;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/3.
 */
public interface IQinniuBiz {
    /**
     *上传
     * @param inputStream 文件流
     * @param fileName 文件名称
     * @return
     * @throws Exception
     */
    public DefaultPutRet upload(InputStream inputStream, String fileName) throws Exception;

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
     *获取缩略图地址列表
     * @param fileNames 文件名，多个用逗号","分割
     * @param width 缩略图的宽
     * @param height 缩略图的高
     * @return
     * @throws Exception
     */
    public List<String> getThumbnails(String fileNames, int width, int height) throws Exception;

}
