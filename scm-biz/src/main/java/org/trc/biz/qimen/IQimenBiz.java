package org.trc.biz.qimen;

import com.taobao.api.internal.spi.CheckResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hzszy
 */
public interface IQimenBiz {
    /**
     * 奇门回调验签
     */
    CheckResult checkResult(HttpServletRequest request, String targetAppSecret);
}
