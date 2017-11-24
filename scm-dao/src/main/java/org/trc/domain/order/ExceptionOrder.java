package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by hzcyn on 2017/11/13.
 */
public class ExceptionOrder implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 渠道编码
    private String channelCode;
    //拆单异常单编号
    private String exceptionOrderCode;
    //店铺订单编码
    private String shopOrderCode;
    //平台订单编码
    private String platformOrderCode;
    //订单所属的店铺id
    private Long shopId;
    //店铺名称
    private String shopName;
    //状态:1-待了结,2-已了结
    private Integer status;
    //异常类型:1-缺货退回,2-缺货等待
    private Integer exceptionType;
    //收货人姓名
    private String receiverName;
    //收货人手机号码
    private String receiverMobile;
    //异常商品数量
    private Integer itemNum;
    //创建时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    // 更新时间
    private Date updateTime;
    //销售渠道编号
    private String sellCode;


    /**
     * 异常订单商品明细列表
     */
    @Transient
    private List<ExceptionOrderItem> exceptionOrderItemList;

    /**
     * 平台订单信息
     */
    @Transient
    private PlatformOrder platformOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getExceptionOrderCode() {
        return exceptionOrderCode;
    }

    public void setExceptionOrderCode(String exceptionOrderCode) {
        this.exceptionOrderCode = exceptionOrderCode;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<ExceptionOrderItem> getExceptionOrderItemList() {
        return exceptionOrderItemList;
    }

    public void setExceptionOrderItemList(List<ExceptionOrderItem> exceptionOrderItemList) {
        this.exceptionOrderItemList = exceptionOrderItemList;
    }

    public PlatformOrder getPlatformOrder() {
        return platformOrder;
    }

    public void setPlatformOrder(PlatformOrder platformOrder) {
        this.platformOrder = platformOrder;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    @Override
    public String toString() {
        return "ExceptionOrder{" +
                "id=" + id +
                ", channelCode='" + channelCode + '\'' +
                ", exceptionOrderCode='" + exceptionOrderCode + '\'' +
                ", shopOrderCode='" + shopOrderCode + '\'' +
                ", platformOrderCode='" + platformOrderCode + '\'' +
                ", shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", status=" + status +
                ", exceptionType=" + exceptionType +
                ", receiverName='" + receiverName + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", itemNum=" + itemNum +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", sellCode='" + sellCode + '\'' +
                ", exceptionOrderItemList=" + exceptionOrderItemList +
                ", platformOrder=" + platformOrder +
                '}';
    }
}
