package org.trc.biz.impl.qimen;

import com.alibaba.fastjson.JSON;
import com.taobao.api.internal.spi.CheckResult;
import com.taobao.api.internal.spi.SpiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.biz.qimen.IQimenBiz;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author hzszy
 */
@Service("qimenBiz")
public class QimenBiz implements IQimenBiz {
    private Logger logger = LoggerFactory.getLogger(QimenBiz.class);

    @Value("${qimen.secret}")
    private String secret;

    @Override
    public CheckResult checkResult( HttpServletRequest request,String targetAppSecret) {
        //这里执行验签逻辑
        CheckResult result = null;
        try {
            result = SpiUtils.checkSign(request, secret);
        } catch (IOException e) {
            logger.error("奇门验证签名异常",e);
        }
       logger.info(JSON.toJSONString(result.isSuccess()));
        return result;
    }
}
