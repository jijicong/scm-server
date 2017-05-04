package org.trc.biz.impl;

import com.qiniu.storage.model.DefaultPutRet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IQinniuBiz;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.FileException;
import org.trc.service.IQinniuService;
import org.trc.util.CommonUtil;

import java.io.InputStream;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Service("qinniuBiz")
public class QinniuBiz implements IQinniuBiz{

    private final static Logger log = LoggerFactory.getLogger(QinniuBiz.class);

    @Autowired
    private IQinniuService qinniuService;

    @Override
    public DefaultPutRet upload(InputStream inputStream, String fileName) throws Exception {
        DefaultPutRet defaultPutRet = null;
        try{
            defaultPutRet = qinniuService.upload(inputStream, fileName);
        }catch (Exception e){
            String msg = CommonUtil.joinStr("上传文件",fileName,"异常").toString();
            log.error(msg,e);
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, msg);
        }
        return defaultPutRet;
    }

    @Override
    public String download(String fileName) throws Exception {
        String url = "";
        try{
            url = qinniuService.download(fileName);
        }catch (Exception e){
            String msg = CommonUtil.joinStr("下载文件",fileName,"异常").toString();
            log.error(msg,e);
            throw new FileException(ExceptionEnum.FILE_DOWNLOAD_EXCEPTION, msg);
        }
        return url;
    }

    @Override
    public String getThumbnail(String fileName, int width, int height) throws Exception {
        String url = "";
        try{
            url = qinniuService.getThumbnail(fileName, width, height);
        }catch (Exception e){
            String msg = CommonUtil.joinStr("查看文件",fileName,"缩略图异常").toString();
            log.error(msg,e);
            throw new FileException(ExceptionEnum.FILE_SHOW_EXCEPTION, msg);
        }
        return url;
    }

    @Override
    public List<String> getThumbnails(String fileNames, int width, int height) throws Exception {
        return null;
    }
}
