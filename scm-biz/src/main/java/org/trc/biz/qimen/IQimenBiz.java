package org.trc.biz.qimen;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hzszy
 */
public interface IQimenBiz {
    /**
     * 奇门回调验签
     */
    void  checkResult( HttpServletRequest request,String targetAppSecret);
}
