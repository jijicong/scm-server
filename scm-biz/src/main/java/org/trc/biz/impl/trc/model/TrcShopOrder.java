package org.trc.biz.impl.trc.model;

public class TrcShopOrder {
    private String id;

    // ä¸»è®¢å•ID
    private String orderId;

    // è®¢å•æ‰€å±žçš„åº—é“ºid
    private Integer shopId;

    // åº—é“ºåç§°
    private String shopName;

    // ä¼šå‘˜id
    private Integer userId;

    // é…é€æ¨¡æ¿ids(1,2,3)
    private String dlytmplIds;

    // å­è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
    private String status;

    private String isDel;

    // å®žä»˜é‡‘é¢,è®¢å•æœ€ç»ˆæ€»é¢
    private Double payment;

    // å„å­è®¢å•ä¸­å•†å“price * numçš„å’Œï¼Œä¸åŒ…æ‹¬ä»»ä½•ä¼˜æƒ ä¿¡æ¯
    private Double totalFee;

    // é‚®è´¹åˆ†æ‘Š
    private Double postFee;

    // ä¿ƒé”€ä¼˜æƒ æ€»é‡‘é¢
    private Double discountPromotion;

    // åº—é“ºä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
    private Double discountCouponShop;

    // å¹³å°ä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
    private Double discountCouponPlatform;

    // ä¿ƒé”€ä¼˜æƒ é‡‘é¢
    private Double discountFee;

    // äº¤æ˜“æ ‡é¢˜
    private String title;

    // ä¹°å®¶ç•™è¨€
    private String buyerMessage;

    // å–å®¶æ‰‹å·¥è°ƒæ•´é‡‘é¢,å­è®¢å•è°ƒæ•´é‡‘é¢ä¹‹å’Œ
    private Double adjustFee;

    // å­è®¢å•å•†å“è´­ä¹°æ•°é‡æ€»æ•°
    private Integer itemNum;

    // å•†å“é‡é‡
    private Double totalWeight;

    // è¯„ä»·çŠ¶æ€
    private Boolean rateStatus;

    // æ˜¯å¦æ˜¯å¤šæ¬¡å‘è´§çš„è®¢å•
    private Boolean isPartConsign;

    // æ‹¼å›¢çŠ¶æ€
    private String groupBuyStatus;

    // è®¢å•æ€»ç¨Žè´¹
    private Double totalTax;

    private Integer createdTime;

    private Integer consignTime;

    private Integer modifiedTime;
    // å–å®¶å¤‡æ³¨
    private String shopMemo;

    // äº¤æ˜“å¤‡æ³¨
    private String tradeMemo;

    /**
     * 返回å–å®¶å¤‡æ³¨
     * @return å–å®¶å¤‡æ³¨
     */
    public String getShopMemo() {
        return shopMemo;
    }

    /**
     * 设置å–å®¶å¤‡æ³¨
     */
    public void setShopMemo(String shopMemo) {
        this.shopMemo = shopMemo == null ? null : shopMemo.trim();
    }

    /**
     * 返回äº¤æ˜“å¤‡æ³¨
     * @return äº¤æ˜“å¤‡æ³¨
     */
    public String getTradeMemo() {
        return tradeMemo;
    }

    /**
     * 设置äº¤æ˜“å¤‡æ³¨
     */
    public void setTradeMemo(String tradeMemo) {
        this.tradeMemo = tradeMemo == null ? null : tradeMemo.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 返回ä¸»è®¢å•ID
     * @return ä¸»è®¢å•ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置ä¸»è®¢å•ID
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 返回è®¢å•æ‰€å±žçš„åº—é“ºid
     * @return è®¢å•æ‰€å±žçš„åº—é“ºid
     */
    public Integer getShopId() {
        return shopId;
    }

    /**
     * 设置è®¢å•æ‰€å±žçš„åº—é“ºid
     */
    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    /**
     * 返回åº—é“ºåç§°
     * @return åº—é“ºåç§°
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * 设置åº—é“ºåç§°
     */
    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    /**
     * 返回ä¼šå‘˜id
     * @return ä¼šå‘˜id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置ä¼šå‘˜id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 返回é…é€æ¨¡æ¿ids(1,2,3)
     * @return é…é€æ¨¡æ¿ids(1,2,3)
     */
    public String getDlytmplIds() {
        return dlytmplIds;
    }

    /**
     * 设置é…é€æ¨¡æ¿ids(1,2,3)
     */
    public void setDlytmplIds(String dlytmplIds) {
        this.dlytmplIds = dlytmplIds == null ? null : dlytmplIds.trim();
    }

    /**
     * 返回å­è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     * @return å­è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置å­è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel == null ? null : isDel.trim();
    }

    /**
     * 返回å®žä»˜é‡‘é¢,è®¢å•æœ€ç»ˆæ€»é¢
     * @return å®žä»˜é‡‘é¢,è®¢å•æœ€ç»ˆæ€»é¢
     */
    public Double getPayment() {
        return payment;
    }

    /**
     * 设置å®žä»˜é‡‘é¢,è®¢å•æœ€ç»ˆæ€»é¢
     */
    public void setPayment(Double payment) {
        this.payment = payment;
    }

    /**
     * 返回å„å­è®¢å•ä¸­å•†å“price * numçš„å’Œï¼Œä¸åŒ…æ‹¬ä»»ä½•ä¼˜æƒ ä¿¡æ¯
     * @return å„å­è®¢å•ä¸­å•†å“price * numçš„å’Œï¼Œä¸åŒ…æ‹¬ä»»ä½•ä¼˜æƒ ä¿¡æ¯
     */
    public Double getTotalFee() {
        return totalFee;
    }

    /**
     * 设置å„å­è®¢å•ä¸­å•†å“price * numçš„å’Œï¼Œä¸åŒ…æ‹¬ä»»ä½•ä¼˜æƒ ä¿¡æ¯
     */
    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * 返回é‚®è´¹åˆ†æ‘Š
     * @return é‚®è´¹åˆ†æ‘Š
     */
    public Double getPostFee() {
        return postFee;
    }

    /**
     * 设置é‚®è´¹åˆ†æ‘Š
     */
    public void setPostFee(Double postFee) {
        this.postFee = postFee;
    }

    /**
     * 返回ä¿ƒé”€ä¼˜æƒ æ€»é‡‘é¢
     * @return ä¿ƒé”€ä¼˜æƒ æ€»é‡‘é¢
     */
    public Double getDiscountPromotion() {
        return discountPromotion;
    }

    /**
     * 设置ä¿ƒé”€ä¼˜æƒ æ€»é‡‘é¢
     */
    public void setDiscountPromotion(Double discountPromotion) {
        this.discountPromotion = discountPromotion;
    }

    /**
     * 返回åº—é“ºä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     * @return åº—é“ºä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     */
    public Double getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置åº—é“ºä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     */
    public void setDiscountCouponShop(Double discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回å¹³å°ä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     * @return å¹³å°ä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     */
    public Double getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置å¹³å°ä¼˜æƒ å·åˆ†æ‘Šæ€»é‡‘é¢
     */
    public void setDiscountCouponPlatform(Double discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回ä¿ƒé”€ä¼˜æƒ é‡‘é¢
     * @return ä¿ƒé”€ä¼˜æƒ é‡‘é¢
     */
    public Double getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置ä¿ƒé”€ä¼˜æƒ é‡‘é¢
     */
    public void setDiscountFee(Double discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回äº¤æ˜“æ ‡é¢˜
     * @return äº¤æ˜“æ ‡é¢˜
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置äº¤æ˜“æ ‡é¢˜
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * 返回ä¹°å®¶ç•™è¨€
     * @return ä¹°å®¶ç•™è¨€
     */
    public String getBuyerMessage() {
        return buyerMessage;
    }

    /**
     * 设置ä¹°å®¶ç•™è¨€
     */
    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage == null ? null : buyerMessage.trim();
    }

    /**
     * 返回å–å®¶æ‰‹å·¥è°ƒæ•´é‡‘é¢,å­è®¢å•è°ƒæ•´é‡‘é¢ä¹‹å’Œ
     * @return å–å®¶æ‰‹å·¥è°ƒæ•´é‡‘é¢,å­è®¢å•è°ƒæ•´é‡‘é¢ä¹‹å’Œ
     */
    public Double getAdjustFee() {
        return adjustFee;
    }

    /**
     * 设置å–å®¶æ‰‹å·¥è°ƒæ•´é‡‘é¢,å­è®¢å•è°ƒæ•´é‡‘é¢ä¹‹å’Œ
     */
    public void setAdjustFee(Double adjustFee) {
        this.adjustFee = adjustFee;
    }

    /**
     * 返回å­è®¢å•å•†å“è´­ä¹°æ•°é‡æ€»æ•°
     * @return å­è®¢å•å•†å“è´­ä¹°æ•°é‡æ€»æ•°
     */
    public Integer getItemNum() {
        return itemNum;
    }

    /**
     * 设置å­è®¢å•å•†å“è´­ä¹°æ•°é‡æ€»æ•°
     */
    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    /**
     * 返回å•†å“é‡é‡
     * @return å•†å“é‡é‡
     */
    public Double getTotalWeight() {
        return totalWeight;
    }

    /**
     * 设置å•†å“é‡é‡
     */
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * 返回è¯„ä»·çŠ¶æ€
     * @return è¯„ä»·çŠ¶æ€
     */
    public Boolean getRateStatus() {
        return rateStatus;
    }

    /**
     * 设置è¯„ä»·çŠ¶æ€
     */
    public void setRateStatus(Boolean rateStatus) {
        this.rateStatus = rateStatus;
    }

    /**
     * 返回æ˜¯å¦æ˜¯å¤šæ¬¡å‘è´§çš„è®¢å•
     * @return æ˜¯å¦æ˜¯å¤šæ¬¡å‘è´§çš„è®¢å•
     */
    public Boolean getIsPartConsign() {
        return isPartConsign;
    }

    /**
     * 设置æ˜¯å¦æ˜¯å¤šæ¬¡å‘è´§çš„è®¢å•
     */
    public void setIsPartConsign(Boolean isPartConsign) {
        this.isPartConsign = isPartConsign;
    }

    /**
     * 返回æ‹¼å›¢çŠ¶æ€
     * @return æ‹¼å›¢çŠ¶æ€
     */
    public String getGroupBuyStatus() {
        return groupBuyStatus;
    }

    /**
     * 设置æ‹¼å›¢çŠ¶æ€
     */
    public void setGroupBuyStatus(String groupBuyStatus) {
        this.groupBuyStatus = groupBuyStatus == null ? null : groupBuyStatus.trim();
    }

    /**
     * 返回è®¢å•æ€»ç¨Žè´¹
     * @return è®¢å•æ€»ç¨Žè´¹
     */
    public Double getTotalTax() {
        return totalTax;
    }

    /**
     * 设置è®¢å•æ€»ç¨Žè´¹
     */
    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    public Integer getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Integer createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Integer consignTime) {
        this.consignTime = consignTime;
    }

    public Integer getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Integer modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}