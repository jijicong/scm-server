package org.trc.biz.impl.qinniu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.storage.model.DefaultPutRet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.config.BaseThumbnailSize;
import org.trc.config.PropertyThumbnailSize;
import org.trc.constants.SupplyConstants;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.PicTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.FileException;
import org.trc.form.FileUrl;
import org.trc.service.IQinniuService;
import org.trc.service.impl.QinniuService;
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
    @Autowired
    private BaseThumbnailSize baseThumbnailSize;

    @Override
    public String upload(InputStream inputStream, String fileName, String module) throws Exception {
        //文件名称检查
        fileTypeCheck(fileName);
        if(null == inputStream){
            String msg = String.format("%s%s%s", "上传文件", fileName, "为空");
            log.error(msg);
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, msg);
        }
        DefaultPutRet defaultPutRet = null;
        try{
            BaseThumbnailSize baseThumbnailSize = getBaseThumbnailSize(module);
            fileName = module + "/" + fileName;
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
        //文件名称检查
        fileTypeCheck(fileName);
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
        //文件名称检查
        fileTypeCheck(fileName);
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
    public List<FileUrl> batchGetFileUrl(String[] fileNames, String thumbnail) throws Exception {
        List<String> files = new ArrayList<String>();
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), thumbnail)){
            for(String fileName : fileNames){
                String[] fileNameSplit = fileName.split("\\"+QinniuService.FILE_FLAG);
                //缩略图名称
                String thumbnailName = String.format("%s_%s_%s%s%s", fileNameSplit[0], baseThumbnailSize.getThumbnailSizes().get(0).getWidth(), baseThumbnailSize.getThumbnailSizes().get(0).getHeight(), QinniuService.FILE_FLAG, fileNameSplit[1]);
                files.add(thumbnailName);
            }
            List<FileUrl> fileUrls = qinniuService.batchGetFileUrl(files.toArray(new String[files.size()]));
            for(int i=0; i< fileUrls.size(); i++){
                fileUrls.get(i).setFileKey(fileNames[i]);
            }
            return fileUrls;
        }else{
            return qinniuService.batchGetFileUrl(fileNames);
        }
    }

    @Override
    public Map<String, Object> batchDelete(String[] fileNames, String module) throws Exception {
        BaseThumbnailSize baseThumbnailSize = getBaseThumbnailSize(module);
        return qinniuService.batchDelete(fileNames, baseThumbnailSize);
    }


    /**
     * 文件名称检查
     * @param fileName
     */
    private void fileTypeCheck(String fileName){
        if(StringUtils.isEmpty(fileName)){
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, "文件名称为空");
        }
        if(fileName.indexOf(QinniuService.FILE_FLAG) == -1){
            String msg = String.format("%s%s%s", "文件名", fileName, "名称错误");
            log.error(msg);
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, msg);
        }
        String[] tmps = fileName.split("\\"+QinniuService.FILE_FLAG);
        JSONArray jsonArray = PicTypeEnum.toJSONArray();
        boolean flag = false;
        for(Object obj : jsonArray){
            JSONObject json = (JSONObject)obj;
            if(StringUtils.equals(json.getString("code"), tmps[1].toUpperCase())){
                flag = true;
                break;
            }
        }
        if(!flag){
            String msg = String.format("%s%s%s", "文件名", fileName, "不是允许的图片格式");
            log.error(msg);
            throw new FileException(ExceptionEnum.FILE_UPLOAD_EXCEPTION, msg);
        }
    }

    private BaseThumbnailSize getBaseThumbnailSize(String module){
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
        return baseThumbnailSize;
    }

}
