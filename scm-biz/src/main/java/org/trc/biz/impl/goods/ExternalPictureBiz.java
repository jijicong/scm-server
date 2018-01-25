package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.IExternalPictureBiz;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.domain.goods.ExternalPicture;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.goods.ExternalPictureForm;
import org.trc.service.goods.IExternalPictureService;
import org.trc.util.Pagenation;
import org.trc.util.URLAvailability;
import tk.mybatis.mapper.entity.Example;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

@Service("externalPictureBiz")
public class ExternalPictureBiz implements IExternalPictureBiz{

    private Logger log = LoggerFactory.getLogger(ExternalPictureBiz.class);

    //分页查询每页数量
    private final static int PAGE_SIZE = 3;

    @Autowired
    private IQinniuBiz qinniuBiz;
    @Autowired
    private IExternalPictureService externalPictureService;

    @Override
    public void uploadExternalPic() {
        long totalCount = 0;
        do {
            ExternalPictureForm form = new ExternalPictureForm();
            Example example = new Example(ExternalPicture.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", Integer.parseInt(ZeroToNineEnum.ZERO.getCode()));
            Pagenation<ExternalPicture> pagenation = new Pagenation<>();
            pagenation.setPageNo(1);
            pagenation.setStart(0);
            pagenation.setPageSize(PAGE_SIZE);
            pagenation = externalPictureService.pagination(example, pagenation, form);
            totalCount = pagenation.getTotalCount() - pagenation.getResult().size();
            for(ExternalPicture externalPicture: pagenation.getResult()){
                try {
                    boolean isUrlValid = URLAvailability.checkUrl(externalPicture.getUrl());
                    if(isUrlValid){
                        String key = qinniuBiz.fetch(externalPicture.getUrl(), externalPicture.getFilePath());
                        if(StringUtils.isNotBlank(key)){
                            externalPicture.setStatus(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));
                        }
                    }else{
                        externalPicture.setStatus(Integer.parseInt(ZeroToNineEnum.TWO.getCode()));
                        log.error(String.format("代发商品%s图片%s无效", externalPicture.getSkuCode(), externalPicture.getUrl()));
                    }
                    externalPicture.setUpdateTime(Calendar.getInstance().getTime());
                    externalPictureService.updateByPrimaryKeySelective(externalPicture);
                } catch (Exception e) {
                    log.error("上传代发商品图片到七牛定时任务上传图片异常", e);
                }
            }
        }while (totalCount > 0);
    }

}
