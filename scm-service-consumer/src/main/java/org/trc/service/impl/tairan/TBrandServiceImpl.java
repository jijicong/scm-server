package org.trc.service.impl.tairan;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.trc.domain.category.Brand;
import org.trc.service.impl.tairan.util.CommomUtils;
import org.trc.service.tairan.TBrandService;
import org.trc.service.tairan.model.BrandToTrc;
import org.trc.service.tairan.model.ResultModel;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;


/**
 * 泰然城渠道品牌交互
 * Created by hzdzf on 2017/5/22.
 */
@Service("tBrandService")
public class TBrandServiceImpl implements TBrandService {

    private Logger logger = LoggerFactory.getLogger(TBrandServiceImpl.class);

    @Value("${tairan.key}")
    private String tairanKey;

    private final static String OR = "|";

    private final static String UNDER_LINE = "_";

    public void setTairanKey(String tairanKey) {
        this.tairanKey = tairanKey;
        System.out.println(tairanKey);
    }

    @Transactional
    @Override
    public ResultModel sendBrandNotice(String action, Brand oldBrand, Brand brand, long operateTime) throws Exception {

        Assert.notNull(brand.getAlise(), "品牌别名不能为空");
        Assert.notNull(brand.getBrandCode(), "品牌编码不能为空");
        Assert.notNull(brand.getIsValid(), "是否停用不能为空");
        Assert.notNull(brand.getLogo(), "图片路径不能为空");
        Assert.notNull(brand.getName(), "品牌名称不能为空");
        Assert.notNull(brand.getWebUrl(), "品牌网址不能为空");

        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setAlise(brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode());
        brandToTrc.setIsValid(brand.getIsValid());
        brandToTrc.setLogo(brand.getLogo());
        brandToTrc.setName(brand.getName());
        brandToTrc.setWebUrl(brand.getWebUrl());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tairanKey).append(OR).append(action).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(brandToTrc.getAlise()).append(OR).append(brandToTrc.getBrandCode()).append(OR).append(brandToTrc.getIsValid()).append(OR).
                append(brandToTrc.getLogo()).append(OR).append(brandToTrc.getName()).append(OR).append(brandToTrc.getWebUrl());

        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        logger.info(params.toJSONString());
        String result = HttpClientUtil.httpPostJsonRequest(CommomUtils.getBrandUrl(), params.toJSONString(), 10000);
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        return resultModel;
    }

    public static void main(String[] args) throws Exception {
        String action = "delete";
        String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setWebUrl("wqeqeqr");
        brandToTrc.setAlise("qwqwedqdeqd");
        brandToTrc.setName("wdad");
        brandToTrc.setBrandCode("vdfgdghd");
        long operateTime = System.currentTimeMillis();
        //model中字段以字典序排序

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("gyl-tairan").append(OR).append(action).append(OR).append(operateTime).append(OR).append(noticeNum).append(OR).
                append(brandToTrc.getAlise()).append(OR).append(brandToTrc.getBrandCode()).append(OR).append(brandToTrc.getIsValid()).append(OR).
                append(brandToTrc.getLogo()).append(OR).append(brandToTrc.getName()).append(OR).append(brandToTrc.getWebUrl());
        //MD5加密
        System.out.println(stringBuilder.toString());
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        System.out.println(params.toJSONString());
        String result = HttpClientUtil.httpPostJsonRequest("http://localhost:8080/scm/example/brand", params.toJSONString(), 10000);
        System.out.println(result);
    }
}
