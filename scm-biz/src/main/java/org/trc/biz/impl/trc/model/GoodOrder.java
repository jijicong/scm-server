package org.trc.biz.impl.trc.model;

public class GoodOrder {
    // å•†å“è®¢å•ID
    private String id;

    // ä»“çº§è®¢å•ID
    private String repoOrderId;

    // åº—é“ºè®¢å•ID
    private String shopOrderId;

    // ä¸»è®¢å•ID
    private String orderId;

    // ä»“åº“ID
    private Integer repoId;

    // ä»“åº“åç§°
    private String repoName;

    // ä¸‰çº§ç±»ç›®id
    private Integer catId;

    // ä¸€çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
    private String catPrimaryName;

    // äºŒçº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
    private String catSecondaryName;

    // ä¸‰çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
    private String catTertiaryName;

    // æ‰€å±žå•†å®¶
    private Integer shopId;

    // åº—é“ºåç§°
    private String shopName;

    // ä¹°å®¶id
    private Integer userId;

    // å•†å“id
    private Integer itemId;

    // è´§å“id
    private Integer skuId;//

    // å•†å“è´§å·
    private String artNo;

    // å•†å“æ¡å½¢ç 
    private String barcode;

    // å•†å“æ ‡é¢˜
    private String title;

    // å•†å“ä»·æ ¼
    private Double price;

    // å¸‚åœºä»·
    private Double marketPrice;

    // ä¿ƒé”€ä»·
    private Double promotionPrice;

    // æŠ¥å…³å•ä»·
    private Double customsPrice;

    // æˆäº¤å•ä»·
    private Double transactionPrice;

    // è´­ä¹°æ•°é‡
    private Integer num;

    // æ˜Žç»†å•†å“å‘è´§æ•°é‡
    private Integer sendNum;

    // SKUçš„å€¼
    private String skuPropertiesName;

    // æœ€è¿‘é€€æ¬¾ID
    private String refundId;

    // æ˜¯å¦è¶…å–
    private Boolean isOversold;

    // è¿é€æ–¹å¼
    private String shippingType;

    // æ†ç»‘çš„å­è®¢å•å·
    private String bindOid;

    // å­è®¢å•å‘è´§çš„å¿«é€’å…¬å¸
    private String logisticsCompany;

    // å­è®¢å•æ‰€åœ¨åŒ…è£¹çš„è¿å•å·
    private String invoiceNo;

    // è¿è´¹åˆ†æ‘Š
    private Double postDiscount;

    // ä¿ƒé”€ä¼˜æƒ åˆ†æ‘Š
    private Double discountPromotion;

    // åº—é“ºä¼˜æƒ å·åˆ†æ‘Šé‡‘é¢
    private Double discountCouponShop;

    // å¹³å°ä¼˜æƒ å·ä¼˜æƒ åˆ†æ‘Š
    private Double discountCouponPlatform;

    // å­è®¢å•çº§è®¢å•ä¼˜æƒ é‡‘é¢
    private Double discountFee;

    // åº”ä»˜é‡‘é¢
    private Double totalFee;

    // å®žä»˜é‡‘é¢
    private Double payment;

    // å•†å“é‡é‡
    private Double totalWeight;

    // æ‰‹å·¥è°ƒæ•´é‡‘é¢
    private Double adjustFee;

    // å­è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
    private String status;

    // å”®åŽçŠ¶æ€
    private String afterSalesStatus;

    // è®¢å•æŠ•è¯‰çŠ¶æ€
    private String complaintsStatus;

    // é€€æ¬¾é‡‘é¢
    private Double refundFee;

    // å•†å®¶ä¸‰çº§ç±»ç›®ç­¾çº¦ä½£é‡‘æ¯”ä¾‹
    private Double catServiceRate;

    // å•†å“å›¾ç‰‡ç»å¯¹è·¯å¾„
    private String picPath;

    // å•†å®¶å¤–éƒ¨ç¼–ç 
    private String outerIid;

    // å•†å®¶å¤–éƒ¨skuç 
    private String outerSkuId;

    // æ˜¯å¦æ”¯æŒä¸‹å•å‡åº“å­˜
    private Boolean subStock;

    // é…é€æ¨¡æ¿id
    private Integer dlytmplId;

    // ä¾›åº”å•†åç§°
    private String supplierName;

    // å•†å“ç¨Žè´¹
    private Double priceTax;

    // è®¢å•åº”ç”¨ä¿ƒé”€æ ‡ç­¾
    private String promotionTags;

    // è®¢å•å•†å“ç±»åž‹
    private String objType;

    // è®¢å•ç±»åž‹ 0-æ™®é€š 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-å›¢è´­
    private String type;

    // ç¨ŽçŽ‡
    private Double taxRate;

    // è®¢å•å†—ä½™å‚æ•°
    private String params;

    private Integer createdTime;

    private Integer payTime;

    private Integer consignTime;

    private Integer modifiedTime;

    private Integer timeoutActionTime;

    private Integer endTime;

    // skuæè¿°
    private String specNatureInfo;

    /**
     * 返回å•†å“è®¢å•ID
     * @return å•†å“è®¢å•ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置å•†å“è®¢å•ID
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 返回ä»“çº§è®¢å•ID
     * @return ä»“çº§è®¢å•ID
     */
    public String getRepoOrderId() {
        return repoOrderId;
    }

    /**
     * 设置ä»“çº§è®¢å•ID
     */
    public void setRepoOrderId(String repoOrderId) {
        this.repoOrderId = repoOrderId == null ? null : repoOrderId.trim();
    }

    /**
     * 返回åº—é“ºè®¢å•ID
     * @return åº—é“ºè®¢å•ID
     */
    public String getShopOrderId() {
        return shopOrderId;
    }

    /**
     * 设置åº—é“ºè®¢å•ID
     */
    public void setShopOrderId(String shopOrderId) {
        this.shopOrderId = shopOrderId == null ? null : shopOrderId.trim();
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
     * 返回ä»“åº“ID
     * @return ä»“åº“ID
     */
    public Integer getRepoId() {
        return repoId;
    }

    /**
     * 设置ä»“åº“ID
     */
    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    /**
     * 返回ä»“åº“åç§°
     * @return ä»“åº“åç§°
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * 设置ä»“åº“åç§°
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName == null ? null : repoName.trim();
    }

    /**
     * 返回ä¸‰çº§ç±»ç›®id
     * @return ä¸‰çº§ç±»ç›®id
     */
    public Integer getCatId() {
        return catId;
    }

    /**
     * 设置ä¸‰çº§ç±»ç›®id
     */
    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    /**
     * 返回ä¸€çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     * @return ä¸€çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public String getCatPrimaryName() {
        return catPrimaryName;
    }

    /**
     * 设置ä¸€çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public void setCatPrimaryName(String catPrimaryName) {
        this.catPrimaryName = catPrimaryName == null ? null : catPrimaryName.trim();
    }

    /**
     * 返回äºŒçº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     * @return äºŒçº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public String getCatSecondaryName() {
        return catSecondaryName;
    }

    /**
     * 设置äºŒçº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public void setCatSecondaryName(String catSecondaryName) {
        this.catSecondaryName = catSecondaryName == null ? null : catSecondaryName.trim();
    }

    /**
     * 返回ä¸‰çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     * @return ä¸‰çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public String getCatTertiaryName() {
        return catTertiaryName;
    }

    /**
     * 设置ä¸‰çº§åˆ†ç±»åç§°ï¼ˆå†—ä½™ï¼‰
     */
    public void setCatTertiaryName(String catTertiaryName) {
        this.catTertiaryName = catTertiaryName == null ? null : catTertiaryName.trim();
    }

    /**
     * 返回æ‰€å±žå•†å®¶
     * @return æ‰€å±žå•†å®¶
     */
    public Integer getShopId() {
        return shopId;
    }

    /**
     * 设置æ‰€å±žå•†å®¶
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
     * 返回ä¹°å®¶id
     * @return ä¹°å®¶id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置ä¹°å®¶id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 返回å•†å“id
     * @return å•†å“id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置å•†å“id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 返回è´§å“id
     * @return è´§å“id
     */
    public Integer getSkuId() {
        return skuId;
    }

    /**
     * 设置è´§å“id
     */
    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    /**
     * 返回å•†å“è´§å·
     * @return å•†å“è´§å·
     */
    public String getArtNo() {
        return artNo;
    }

    /**
     * 设置å•†å“è´§å·
     */
    public void setArtNo(String artNo) {
        this.artNo = artNo == null ? null : artNo.trim();
    }

    /**
     * 返回å•†å“æ¡å½¢ç 
     * @return å•†å“æ¡å½¢ç 
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * 设置å•†å“æ¡å½¢ç 
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    /**
     * 返回å•†å“æ ‡é¢˜
     * @return å•†å“æ ‡é¢˜
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置å•†å“æ ‡é¢˜
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * 返回å•†å“ä»·æ ¼
     * @return å•†å“ä»·æ ¼
     */
    public Double getPrice() {
        return price;
    }

    /**
     * 设置å•†å“ä»·æ ¼
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 返回å¸‚åœºä»·
     * @return å¸‚åœºä»·
     */
    public Double getMarketPrice() {
        return marketPrice;
    }

    /**
     * 设置å¸‚åœºä»·
     */
    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    /**
     * 返回ä¿ƒé”€ä»·
     * @return ä¿ƒé”€ä»·
     */
    public Double getPromotionPrice() {
        return promotionPrice;
    }

    /**
     * 设置ä¿ƒé”€ä»·
     */
    public void setPromotionPrice(Double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    /**
     * 返回æŠ¥å…³å•ä»·
     * @return æŠ¥å…³å•ä»·
     */
    public Double getCustomsPrice() {
        return customsPrice;
    }

    /**
     * 设置æŠ¥å…³å•ä»·
     */
    public void setCustomsPrice(Double customsPrice) {
        this.customsPrice = customsPrice;
    }

    /**
     * 返回æˆäº¤å•ä»·
     * @return æˆäº¤å•ä»·
     */
    public Double getTransactionPrice() {
        return transactionPrice;
    }

    /**
     * 设置æˆäº¤å•ä»·
     */
    public void setTransactionPrice(Double transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    /**
     * 返回è´­ä¹°æ•°é‡
     * @return è´­ä¹°æ•°é‡
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置è´­ä¹°æ•°é‡
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 返回æ˜Žç»†å•†å“å‘è´§æ•°é‡
     * @return æ˜Žç»†å•†å“å‘è´§æ•°é‡
     */
    public Integer getSendNum() {
        return sendNum;
    }

    /**
     * 设置æ˜Žç»†å•†å“å‘è´§æ•°é‡
     */
    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    /**
     * 返回SKUçš„å€¼
     * @return SKUçš„å€¼
     */
    public String getSkuPropertiesName() {
        return skuPropertiesName;
    }

    /**
     * 设置SKUçš„å€¼
     */
    public void setSkuPropertiesName(String skuPropertiesName) {
        this.skuPropertiesName = skuPropertiesName == null ? null : skuPropertiesName.trim();
    }

    /**
     * 返回æœ€è¿‘é€€æ¬¾ID
     * @return æœ€è¿‘é€€æ¬¾ID
     */
    public String getRefundId() {
        return refundId;
    }

    /**
     * 设置æœ€è¿‘é€€æ¬¾ID
     */
    public void setRefundId(String refundId) {
        this.refundId = refundId == null ? null : refundId.trim();
    }

    /**
     * 返回æ˜¯å¦è¶…å–
     * @return æ˜¯å¦è¶…å–
     */
    public Boolean getIsOversold() {
        return isOversold;
    }

    /**
     * 设置æ˜¯å¦è¶…å–
     */
    public void setIsOversold(Boolean isOversold) {
        this.isOversold = isOversold;
    }

    /**
     * 返回è¿é€æ–¹å¼
     * @return è¿é€æ–¹å¼
     */
    public String getShippingType() {
        return shippingType;
    }

    /**
     * 设置è¿é€æ–¹å¼
     */
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType == null ? null : shippingType.trim();
    }

    /**
     * 返回æ†ç»‘çš„å­è®¢å•å·
     * @return æ†ç»‘çš„å­è®¢å•å·
     */
    public String getBindOid() {
        return bindOid;
    }

    /**
     * 设置æ†ç»‘çš„å­è®¢å•å·
     */
    public void setBindOid(String bindOid) {
        this.bindOid = bindOid == null ? null : bindOid.trim();
    }

    /**
     * 返回å­è®¢å•å‘è´§çš„å¿«é€’å…¬å¸
     * @return å­è®¢å•å‘è´§çš„å¿«é€’å…¬å¸
     */
    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    /**
     * 设置å­è®¢å•å‘è´§çš„å¿«é€’å…¬å¸
     */
    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany == null ? null : logisticsCompany.trim();
    }

    /**
     * 返回å­è®¢å•æ‰€åœ¨åŒ…è£¹çš„è¿å•å·
     * @return å­è®¢å•æ‰€åœ¨åŒ…è£¹çš„è¿å•å·
     */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /**
     * 设置å­è®¢å•æ‰€åœ¨åŒ…è£¹çš„è¿å•å·
     */
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo == null ? null : invoiceNo.trim();
    }

    /**
     * 返回è¿è´¹åˆ†æ‘Š
     * @return è¿è´¹åˆ†æ‘Š
     */
    public Double getPostDiscount() {
        return postDiscount;
    }

    /**
     * 设置è¿è´¹åˆ†æ‘Š
     */
    public void setPostDiscount(Double postDiscount) {
        this.postDiscount = postDiscount;
    }

    /**
     * 返回ä¿ƒé”€ä¼˜æƒ åˆ†æ‘Š
     * @return ä¿ƒé”€ä¼˜æƒ åˆ†æ‘Š
     */
    public Double getDiscountPromotion() {
        return discountPromotion;
    }

    /**
     * 设置ä¿ƒé”€ä¼˜æƒ åˆ†æ‘Š
     */
    public void setDiscountPromotion(Double discountPromotion) {
        this.discountPromotion = discountPromotion;
    }

    /**
     * 返回åº—é“ºä¼˜æƒ å·åˆ†æ‘Šé‡‘é¢
     * @return åº—é“ºä¼˜æƒ å·åˆ†æ‘Šé‡‘é¢
     */
    public Double getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置åº—é“ºä¼˜æƒ å·åˆ†æ‘Šé‡‘é¢
     */
    public void setDiscountCouponShop(Double discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回å¹³å°ä¼˜æƒ å·ä¼˜æƒ åˆ†æ‘Š
     * @return å¹³å°ä¼˜æƒ å·ä¼˜æƒ åˆ†æ‘Š
     */
    public Double getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置å¹³å°ä¼˜æƒ å·ä¼˜æƒ åˆ†æ‘Š
     */
    public void setDiscountCouponPlatform(Double discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回å­è®¢å•çº§è®¢å•ä¼˜æƒ é‡‘é¢
     * @return å­è®¢å•çº§è®¢å•ä¼˜æƒ é‡‘é¢
     */
    public Double getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置å­è®¢å•çº§è®¢å•ä¼˜æƒ é‡‘é¢
     */
    public void setDiscountFee(Double discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回åº”ä»˜é‡‘é¢
     * @return åº”ä»˜é‡‘é¢
     */
    public Double getTotalFee() {
        return totalFee;
    }

    /**
     * 设置åº”ä»˜é‡‘é¢
     */
    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * 返回å®žä»˜é‡‘é¢
     * @return å®žä»˜é‡‘é¢
     */
    public Double getPayment() {
        return payment;
    }

    /**
     * 设置å®žä»˜é‡‘é¢
     */
    public void setPayment(Double payment) {
        this.payment = payment;
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
     * 返回æ‰‹å·¥è°ƒæ•´é‡‘é¢
     * @return æ‰‹å·¥è°ƒæ•´é‡‘é¢
     */
    public Double getAdjustFee() {
        return adjustFee;
    }

    /**
     * 设置æ‰‹å·¥è°ƒæ•´é‡‘é¢
     */
    public void setAdjustFee(Double adjustFee) {
        this.adjustFee = adjustFee;
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

    /**
     * 返回å”®åŽçŠ¶æ€
     * @return å”®åŽçŠ¶æ€
     */
    public String getAfterSalesStatus() {
        return afterSalesStatus;
    }

    /**
     * 设置å”®åŽçŠ¶æ€
     */
    public void setAfterSalesStatus(String afterSalesStatus) {
        this.afterSalesStatus = afterSalesStatus == null ? null : afterSalesStatus.trim();
    }

    /**
     * 返回è®¢å•æŠ•è¯‰çŠ¶æ€
     * @return è®¢å•æŠ•è¯‰çŠ¶æ€
     */
    public String getComplaintsStatus() {
        return complaintsStatus;
    }

    /**
     * 设置è®¢å•æŠ•è¯‰çŠ¶æ€
     */
    public void setComplaintsStatus(String complaintsStatus) {
        this.complaintsStatus = complaintsStatus == null ? null : complaintsStatus.trim();
    }

    /**
     * 返回é€€æ¬¾é‡‘é¢
     * @return é€€æ¬¾é‡‘é¢
     */
    public Double getRefundFee() {
        return refundFee;
    }

    /**
     * 设置é€€æ¬¾é‡‘é¢
     */
    public void setRefundFee(Double refundFee) {
        this.refundFee = refundFee;
    }

    /**
     * 返回å•†å®¶ä¸‰çº§ç±»ç›®ç­¾çº¦ä½£é‡‘æ¯”ä¾‹
     * @return å•†å®¶ä¸‰çº§ç±»ç›®ç­¾çº¦ä½£é‡‘æ¯”ä¾‹
     */
    public Double getCatServiceRate() {
        return catServiceRate;
    }

    /**
     * 设置å•†å®¶ä¸‰çº§ç±»ç›®ç­¾çº¦ä½£é‡‘æ¯”ä¾‹
     */
    public void setCatServiceRate(Double catServiceRate) {
        this.catServiceRate = catServiceRate;
    }

    /**
     * 返回å•†å“å›¾ç‰‡ç»å¯¹è·¯å¾„
     * @return å•†å“å›¾ç‰‡ç»å¯¹è·¯å¾„
     */
    public String getPicPath() {
        return picPath;
    }

    /**
     * 设置å•†å“å›¾ç‰‡ç»å¯¹è·¯å¾„
     */
    public void setPicPath(String picPath) {
        this.picPath = picPath == null ? null : picPath.trim();
    }

    /**
     * 返回å•†å®¶å¤–éƒ¨ç¼–ç 
     * @return å•†å®¶å¤–éƒ¨ç¼–ç 
     */
    public String getOuterIid() {
        return outerIid;
    }

    /**
     * 设置å•†å®¶å¤–éƒ¨ç¼–ç 
     */
    public void setOuterIid(String outerIid) {
        this.outerIid = outerIid == null ? null : outerIid.trim();
    }

    /**
     * 返回å•†å®¶å¤–éƒ¨skuç 
     * @return å•†å®¶å¤–éƒ¨skuç 
     */
    public String getOuterSkuId() {
        return outerSkuId;
    }

    /**
     * 设置å•†å®¶å¤–éƒ¨skuç 
     */
    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId == null ? null : outerSkuId.trim();
    }

    /**
     * 返回æ˜¯å¦æ”¯æŒä¸‹å•å‡åº“å­˜
     * @return æ˜¯å¦æ”¯æŒä¸‹å•å‡åº“å­˜
     */
    public Boolean getSubStock() {
        return subStock;
    }

    /**
     * 设置æ˜¯å¦æ”¯æŒä¸‹å•å‡åº“å­˜
     */
    public void setSubStock(Boolean subStock) {
        this.subStock = subStock;
    }

    /**
     * 返回é…é€æ¨¡æ¿id
     * @return é…é€æ¨¡æ¿id
     */
    public Integer getDlytmplId() {
        return dlytmplId;
    }

    /**
     * 设置é…é€æ¨¡æ¿id
     */
    public void setDlytmplId(Integer dlytmplId) {
        this.dlytmplId = dlytmplId;
    }

    /**
     * 返回ä¾›åº”å•†åç§°
     * @return ä¾›åº”å•†åç§°
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 设置ä¾›åº”å•†åç§°
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    /**
     * 返回å•†å“ç¨Žè´¹
     * @return å•†å“ç¨Žè´¹
     */
    public Double getPriceTax() {
        return priceTax;
    }

    /**
     * 设置å•†å“ç¨Žè´¹
     */
    public void setPriceTax(Double priceTax) {
        this.priceTax = priceTax;
    }

    /**
     * 返回è®¢å•åº”ç”¨ä¿ƒé”€æ ‡ç­¾
     * @return è®¢å•åº”ç”¨ä¿ƒé”€æ ‡ç­¾
     */
    public String getPromotionTags() {
        return promotionTags;
    }

    /**
     * 设置è®¢å•åº”ç”¨ä¿ƒé”€æ ‡ç­¾
     */
    public void setPromotionTags(String promotionTags) {
        this.promotionTags = promotionTags == null ? null : promotionTags.trim();
    }

    /**
     * 返回è®¢å•å•†å“ç±»åž‹
     * @return è®¢å•å•†å“ç±»åž‹
     */
    public String getObjType() {
        return objType;
    }

    /**
     * 设置è®¢å•å•†å“ç±»åž‹
     */
    public void setObjType(String objType) {
        this.objType = objType == null ? null : objType.trim();
    }

    /**
     * 返回è®¢å•ç±»åž‹ 0-æ™®é€š 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-å›¢è´­
     * @return è®¢å•ç±»åž‹ 0-æ™®é€š 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-å›¢è´­
     */
    public String getType() {
        return type;
    }

    /**
     * 设置è®¢å•ç±»åž‹ 0-æ™®é€š 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-å›¢è´­
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 返回ç¨ŽçŽ‡
     * @return ç¨ŽçŽ‡
     */
    public Double getTaxRate() {
        return taxRate;
    }

    /**
     * 设置ç¨ŽçŽ‡
     */
    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * 返回è®¢å•å†—ä½™å‚æ•°
     * @return è®¢å•å†—ä½™å‚æ•°
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置è®¢å•å†—ä½™å‚æ•°
     */
    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public Integer getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Integer createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getPayTime() {
        return payTime;
    }

    public void setPayTime(Integer payTime) {
        this.payTime = payTime;
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

    public Integer getTimeoutActionTime() {
        return timeoutActionTime;
    }

    public void setTimeoutActionTime(Integer timeoutActionTime) {
        this.timeoutActionTime = timeoutActionTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    /**
     * 返回skuæè¿°
     * @return skuæè¿°
     */
    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    /**
     * 设置skuæè¿°
     */
    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo == null ? null : specNatureInfo.trim();
    }
}