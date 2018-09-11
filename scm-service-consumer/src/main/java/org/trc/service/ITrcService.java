package org.trc.service;


import org.trc.form.AfterSaleNoticeTrcForm;
import org.trc.form.ChannelOrderResponse;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.ReturnInResultNoticeForm;
import org.trc.model.ToGlyResultDO;

/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
public interface ITrcService {

    /**
     * @param brandUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendBrandNotice(String brandUrl, String params) throws Exception;

    /**
     * @param categoryPropertyUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendCategoryPropertyList(String categoryPropertyUrl, String params) throws Exception;

    /**
     * @param categoryBrandUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendCategoryBrandList(String categoryBrandUrl, String params) throws Exception;

    /**
     * @param categoryUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendCategoryToTrc(String categoryUrl, String params) throws Exception;

    /**
     * @param propertyUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendPropertyNotice(String propertyUrl, String params) throws Exception;

    /**
     * @param itemsUrl
     * @param params
     * @return
     * @throws Exception
     */
    ToGlyResultDO sendItemsNotice(String itemsUrl, String params) throws Exception;

    /**
     *
     * @param getJDLogisticUrl
     * @return
     * @throws Exception
     */
    String getJDLogistic(String getJDLogisticUrl) throws Exception;

    /**
     * 发送订单提交结果通知
     * @param channelOrderResponse
     * @return
     */
    ToGlyResultDO sendOrderSubmitResultNotice(ChannelOrderResponse channelOrderResponse);

    /**
     * 发送物流信息通知
     * @param logisticNoticeForm
     * @return
     */
    ToGlyResultDO sendLogisticInfoNotice(LogisticNoticeForm logisticNoticeForm);

    /**
     * 退货入库单收货结果通知
     * @param returnInResultNoticeForm
     * @return
     */
    ToGlyResultDO sendReturnInResult(ReturnInResultNoticeForm returnInResultNoticeForm);

    /**
     * 发送创建通知接口
     */
    ToGlyResultDO createAfterSaleNotice(AfterSaleNoticeTrcForm saleNoticeTrcForm);
}
