package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IQinniuBiz;
import org.trc.config.BaseThumbnailSize;
import org.trc.config.PropertyThumbnailSize;
import org.trc.constants.SupplyConstants;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.FileException;
import org.trc.service.IQinniuService;
import org.trc.util.CommonUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Service("qinniuBiz")
public class QinniuBiz implements IQinniuBiz{

    private final static Logger log = LoggerFactory.getLogger(QinniuBiz.class);

    @Autowired
    private IQinniuService qinniuService;

    @Override
    public String upload(InputStream inputStream, String fileName, String module) throws Exception {
        DefaultPutRet defaultPutRet = null;
        try{
            /**
             * FIXME
             */
            BaseThumbnailSize baseThumbnailSize = new BaseThumbnailSize();
            if(StringUtils.equals(module, SupplyConstants.QinNiu.Module.PROPERTY)){//属性管理
                baseThumbnailSize = new PropertyThumbnailSize();
            }else if(StringUtils.equals(module, SupplyConstants.QinNiu.Module.SUPPLY)){//供应商管理
                //baseThumbnailSize = new PropertyThumbnailSize();
            }else {
                //
            }
            defaultPutRet = qinniuService.upload(inputStream, fileName, baseThumbnailSize);
        }catch (Exception e){
            String msg = CommonUtil.joinStr("上传文件",fileName,"异常").toString();
            log.error(msg,e);
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, msg);
        }
        return defaultPutRet.key;
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
    public Map<String, String> batchGetFileUrl(String[] fileNames) throws Exception {
        return qinniuService.batchGetFileUrl(fileNames);
    }
}
