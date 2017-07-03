package org.trc.biz.impl.trc.model;

public class Order {
    // ä¸»è®¢å•ID
    private String id;

    // ç”¨æˆ·ID
    private Integer userId;

    // ä¼šå‘˜åç§°
    private String userName;

    // ä¹°å®¶è´­ä¹°çš„å•†å“æ€»æ•°
    private Integer itemNum;

    // æ”¯ä»˜ç±»åž‹
    private String payType;

    // å®žä»˜é‡‘é¢
    private Double payment;

    // ç§¯åˆ†æŠµæ‰£é‡‘é¢
    private Double pointsFee;

    // è®¢å•æ€»é‡‘é¢ï¼ˆå•†å“å•ä»·*æ•°é‡ï¼‰
    private Double totalFee;

    // å–å®¶æ‰‹å·¥è°ƒæ•´é‡‘é¢,å­è®¢å•è°ƒæ•´é‡‘é¢ä¹‹å’Œ
    private Double adjustFee;

    // é‚®è´¹
    private Double postFee;

    // æ€»ç¨Žè´¹
    private Double totalTax;

    // æ˜¯å¦å¼€ç¥¨ 1-æ˜¯ 0-ä¸æ˜¯
    private Boolean needInvoice;

    // å‘ç¥¨æŠ¬å¤´
    private String invoiceName;

    // å‘ç¥¨ç±»åž‹
    private String invoiceType;

    // å‘ç¥¨å†…å®¹
    private String invoiceMain;

    // æ”¶è´§äººæ‰€åœ¨çœ
    private String receiverState;

    // æ”¶è´§äººæ‰€åœ¨åŸŽå¸‚
    private String receiverCity;

    // æ”¶è´§äººæ‰€åœ¨åœ°åŒº
    private String receiverDistrict;

    // æ”¶è´§äººè¯¦ç»†åœ°å€
    private String receiverAddress;

    // æ”¶è´§äººé‚®ç¼–
    private String receiverZip;

    // æ”¶è´§äººå§“å
    private String receiverName;

    // æ”¶è´§äººèº«ä»½è¯ä¿¡æ¯
    private String receiverIdNumber;

    // æ”¶è´§äººèº«ä»½è¯æ­£é¢
    private String receiverIdCardFront;

    // æ”¶è´§äººèº«ä»½è¯èƒŒé¢
    private String receiverIdCardBack;

    // æ”¶è´§äººç”µè¯å·ç 
    private String receiverPhone;

    // æ”¶è´§äººæ‰‹æœºå·ç 
    private String receiverMobile;

    // ä¹°å®¶ä¸‹å•åœ°åŒº
    private String buyerArea;

    // è‡ªæå¤‡æ³¨
    private String zitiMemo;

    // è‡ªæåœ°å€
    private String zitiAddr;

    // æ˜¯å¦åŒ¿åä¸‹å• 1-åŒ¿å 0-å®žå
    private Boolean anony;

    // ä¹°å®¶ä¸‹å•é€ç§¯åˆ†
    private Integer obtainPointFee;

    // ä¹°å®¶ä½¿ç”¨ç§¯åˆ†
    private Integer realPointFee;

    // åˆ†é˜¶æ®µä»˜æ¬¾çŠ¶æ€
    private String stepTradeStatus;

    // åˆ†é˜¶æ®µå·²ä»˜é‡‘é¢
    private Double stepPaidFee;

    // æ˜¯å¦ç”Ÿæˆç»“ç®—æ¸…å• 0-å¦ 1-æ˜¯
    private Boolean isClearing;

    // è®¢å•å–æ¶ˆåŽŸå› 
    private String cancelReason;

    // æˆåŠŸï¼šSUCCESS  å¾…é€€æ¬¾ï¼šWAIT_REFUND  é»˜è®¤ï¼šNO_APPLY_CANCEL
    private String cancelStatus;

    // è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
    private String status;

    // æ˜¯å¦ä¸ºè™šæ‹Ÿè®¢å• 0-å¦ 1-æ˜¯
    private Boolean isVirtual;

    // ipåœ°å€
    private String ip;

    // è®¢å•ç±»åž‹ï¼š0-æ™®é€šè®¢å• 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-æ‹¼å›¢ 4-å……å€¼
    private Boolean type;

    // ä¿ƒé”€ä¼˜æƒ æ€»é‡‘é¢
    private Double discountPromotion;

    // åº—é“ºä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
    private Double discountCouponShop;

    // å¹³å°ä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
    private Double discountCouponPlatform;

    // è®¢å•ä¼˜æƒ æ€»é‡‘é¢
    private Double discountFee;

    // é…é€ç±»åž‹
    private String shippingType;

    // è®¢å•æ¥æºå¹³å° ç”µè„‘-pc æ‰‹æœºç½‘é¡µ-wap ç§»åŠ¨ç«¯-app
    private String platform;

    // ç”¨æˆ·è¯„ä»·çŠ¶æ€ 0-æœªè¯„ä»· 1-å·²è¯„ä»·
    private Boolean rateStatus;

    // åº”ç”¨ä¼˜æƒ å·ç 
    private String couponCode;

    // æ‹¼å›¢çŠ¶æ€
    private String groupBuyStatus;

    // æ˜¯å¦åˆ é™¤è®¢å• 0-æœªåˆ é™¤ 1-å·²åˆ é™¤
    private Boolean isDel;

    private Integer createdTime;

    private Integer payTime;

    private Integer consignTime;

    private Integer receiveTime;

    private Integer modifiedTime;

    private Integer timeoutActionTime;

    private Integer endTime;

    private String payBillId;

    /**
     * 返回ä¸»è®¢å•ID
     * @return ä¸»è®¢å•ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ä¸»è®¢å•ID
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 返回ç”¨æˆ·ID
     * @return ç”¨æˆ·ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置ç”¨æˆ·ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 返回ä¼šå‘˜åç§°
     * @return ä¼šå‘˜åç§°
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置ä¼šå‘˜åç§°
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 返回ä¹°å®¶è´­ä¹°çš„å•†å“æ€»æ•°
     * @return ä¹°å®¶è´­ä¹°çš„å•†å“æ€»æ•°
     */
    public Integer getItemNum() {
        return itemNum;
    }

    /**
     * 设置ä¹°å®¶è´­ä¹°çš„å•†å“æ€»æ•°
     */
    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    /**
     * 返回æ”¯ä»˜ç±»åž‹
     * @return æ”¯ä»˜ç±»åž‹
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置æ”¯ä»˜ç±»åž‹
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
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
     * 返回ç§¯åˆ†æŠµæ‰£é‡‘é¢
     * @return ç§¯åˆ†æŠµæ‰£é‡‘é¢
     */
    public Double getPointsFee() {
        return pointsFee;
    }

    /**
     * 设置ç§¯åˆ†æŠµæ‰£é‡‘é¢
     */
    public void setPointsFee(Double pointsFee) {
        this.pointsFee = pointsFee;
    }

    /**
     * 返回è®¢å•æ€»é‡‘é¢ï¼ˆå•†å“å•ä»·*æ•°é‡ï¼‰
     * @return è®¢å•æ€»é‡‘é¢ï¼ˆå•†å“å•ä»·*æ•°é‡ï¼‰
     */
    public Double getTotalFee() {
        return totalFee;
    }

    /**
     * 设置è®¢å•æ€»é‡‘é¢ï¼ˆå•†å“å•ä»·*æ•°é‡ï¼‰
     */
    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
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
     * 返回é‚®è´¹
     * @return é‚®è´¹
     */
    public Double getPostFee() {
        return postFee;
    }

    /**
     * 设置é‚®è´¹
     */
    public void setPostFee(Double postFee) {
        this.postFee = postFee;
    }

    /**
     * 返回æ€»ç¨Žè´¹
     * @return æ€»ç¨Žè´¹
     */
    public Double getTotalTax() {
        return totalTax;
    }

    /**
     * 设置æ€»ç¨Žè´¹
     */
    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * 返回æ˜¯å¦å¼€ç¥¨ 1-æ˜¯ 0-ä¸æ˜¯
     * @return æ˜¯å¦å¼€ç¥¨ 1-æ˜¯ 0-ä¸æ˜¯
     */
    public Boolean getNeedInvoice() {
        return needInvoice;
    }

    /**
     * 设置æ˜¯å¦å¼€ç¥¨ 1-æ˜¯ 0-ä¸æ˜¯
     */
    public void setNeedInvoice(Boolean needInvoice) {
        this.needInvoice = needInvoice;
    }

    /**
     * 返回å‘ç¥¨æŠ¬å¤´
     * @return å‘ç¥¨æŠ¬å¤´
     */
    public String getInvoiceName() {
        return invoiceName;
    }

    /**
     * 设置å‘ç¥¨æŠ¬å¤´
     */
    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName == null ? null : invoiceName.trim();
    }

    /**
     * 返回å‘ç¥¨ç±»åž‹
     * @return å‘ç¥¨ç±»åž‹
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * 设置å‘ç¥¨ç±»åž‹
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    /**
     * 返回å‘ç¥¨å†…å®¹
     * @return å‘ç¥¨å†…å®¹
     */
    public String getInvoiceMain() {
        return invoiceMain;
    }

    /**
     * 设置å‘ç¥¨å†…å®¹
     */
    public void setInvoiceMain(String invoiceMain) {
        this.invoiceMain = invoiceMain == null ? null : invoiceMain.trim();
    }

    /**
     * 返回æ”¶è´§äººæ‰€åœ¨çœ
     * @return æ”¶è´§äººæ‰€åœ¨çœ
     */
    public String getReceiverState() {
        return receiverState;
    }

    /**
     * 设置æ”¶è´§äººæ‰€åœ¨çœ
     */
    public void setReceiverState(String receiverState) {
        this.receiverState = receiverState == null ? null : receiverState.trim();
    }

    /**
     * 返回æ”¶è´§äººæ‰€åœ¨åŸŽå¸‚
     * @return æ”¶è´§äººæ‰€åœ¨åŸŽå¸‚
     */
    public String getReceiverCity() {
        return receiverCity;
    }

    /**
     * 设置æ”¶è´§äººæ‰€åœ¨åŸŽå¸‚
     */
    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity == null ? null : receiverCity.trim();
    }

    /**
     * 返回æ”¶è´§äººæ‰€åœ¨åœ°åŒº
     * @return æ”¶è´§äººæ‰€åœ¨åœ°åŒº
     */
    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    /**
     * 设置æ”¶è´§äººæ‰€åœ¨åœ°åŒº
     */
    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict == null ? null : receiverDistrict.trim();
    }

    /**
     * 返回æ”¶è´§äººè¯¦ç»†åœ°å€
     * @return æ”¶è´§äººè¯¦ç»†åœ°å€
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * 设置æ”¶è´§äººè¯¦ç»†åœ°å€
     */
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    /**
     * 返回æ”¶è´§äººé‚®ç¼–
     * @return æ”¶è´§äººé‚®ç¼–
     */
    public String getReceiverZip() {
        return receiverZip;
    }

    /**
     * 设置æ”¶è´§äººé‚®ç¼–
     */
    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip == null ? null : receiverZip.trim();
    }

    /**
     * 返回æ”¶è´§äººå§“å
     * @return æ”¶è´§äººå§“å
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * 设置æ”¶è´§äººå§“å
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    /**
     * 返回æ”¶è´§äººèº«ä»½è¯ä¿¡æ¯
     * @return æ”¶è´§äººèº«ä»½è¯ä¿¡æ¯
     */
    public String getReceiverIdNumber() {
        return receiverIdNumber;
    }

    /**
     * 设置æ”¶è´§äººèº«ä»½è¯ä¿¡æ¯
     */
    public void setReceiverIdNumber(String receiverIdNumber) {
        this.receiverIdNumber = receiverIdNumber == null ? null : receiverIdNumber.trim();
    }

    /**
     * 返回æ”¶è´§äººèº«ä»½è¯æ­£é¢
     * @return æ”¶è´§äººèº«ä»½è¯æ­£é¢
     */
    public String getReceiverIdCardFront() {
        return receiverIdCardFront;
    }

    /**
     * 设置æ”¶è´§äººèº«ä»½è¯æ­£é¢
     */
    public void setReceiverIdCardFront(String receiverIdCardFront) {
        this.receiverIdCardFront = receiverIdCardFront == null ? null : receiverIdCardFront.trim();
    }

    /**
     * 返回æ”¶è´§äººèº«ä»½è¯èƒŒé¢
     * @return æ”¶è´§äººèº«ä»½è¯èƒŒé¢
     */
    public String getReceiverIdCardBack() {
        return receiverIdCardBack;
    }

    /**
     * 设置æ”¶è´§äººèº«ä»½è¯èƒŒé¢
     */
    public void setReceiverIdCardBack(String receiverIdCardBack) {
        this.receiverIdCardBack = receiverIdCardBack == null ? null : receiverIdCardBack.trim();
    }

    /**
     * 返回æ”¶è´§äººç”µè¯å·ç 
     * @return æ”¶è´§äººç”µè¯å·ç 
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * 设置æ”¶è´§äººç”µè¯å·ç 
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    /**
     * 返回æ”¶è´§äººæ‰‹æœºå·ç 
     * @return æ”¶è´§äººæ‰‹æœºå·ç 
     */
    public String getReceiverMobile() {
        return receiverMobile;
    }

    /**
     * 设置æ”¶è´§äººæ‰‹æœºå·ç 
     */
    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile == null ? null : receiverMobile.trim();
    }

    /**
     * 返回ä¹°å®¶ä¸‹å•åœ°åŒº
     * @return ä¹°å®¶ä¸‹å•åœ°åŒº
     */
    public String getBuyerArea() {
        return buyerArea;
    }

    /**
     * 设置ä¹°å®¶ä¸‹å•åœ°åŒº
     */
    public void setBuyerArea(String buyerArea) {
        this.buyerArea = buyerArea == null ? null : buyerArea.trim();
    }

    /**
     * 返回è‡ªæå¤‡æ³¨
     * @return è‡ªæå¤‡æ³¨
     */
    public String getZitiMemo() {
        return zitiMemo;
    }

    /**
     * 设置è‡ªæå¤‡æ³¨
     */
    public void setZitiMemo(String zitiMemo) {
        this.zitiMemo = zitiMemo == null ? null : zitiMemo.trim();
    }

    /**
     * 返回è‡ªæåœ°å€
     * @return è‡ªæåœ°å€
     */
    public String getZitiAddr() {
        return zitiAddr;
    }

    /**
     * 设置è‡ªæåœ°å€
     */
    public void setZitiAddr(String zitiAddr) {
        this.zitiAddr = zitiAddr == null ? null : zitiAddr.trim();
    }

    /**
     * 返回æ˜¯å¦åŒ¿åä¸‹å• 1-åŒ¿å 0-å®žå
     * @return æ˜¯å¦åŒ¿åä¸‹å• 1-åŒ¿å 0-å®žå
     */
    public Boolean getAnony() {
        return anony;
    }

    /**
     * 设置æ˜¯å¦åŒ¿åä¸‹å• 1-åŒ¿å 0-å®žå
     */
    public void setAnony(Boolean anony) {
        this.anony = anony;
    }

    /**
     * 返回ä¹°å®¶ä¸‹å•é€ç§¯åˆ†
     * @return ä¹°å®¶ä¸‹å•é€ç§¯åˆ†
     */
    public Integer getObtainPointFee() {
        return obtainPointFee;
    }

    /**
     * 设置ä¹°å®¶ä¸‹å•é€ç§¯åˆ†
     */
    public void setObtainPointFee(Integer obtainPointFee) {
        this.obtainPointFee = obtainPointFee;
    }

    /**
     * 返回ä¹°å®¶ä½¿ç”¨ç§¯åˆ†
     * @return ä¹°å®¶ä½¿ç”¨ç§¯åˆ†
     */
    public Integer getRealPointFee() {
        return realPointFee;
    }

    /**
     * 设置ä¹°å®¶ä½¿ç”¨ç§¯åˆ†
     */
    public void setRealPointFee(Integer realPointFee) {
        this.realPointFee = realPointFee;
    }

    /**
     * 返回åˆ†é˜¶æ®µä»˜æ¬¾çŠ¶æ€
     * @return åˆ†é˜¶æ®µä»˜æ¬¾çŠ¶æ€
     */
    public String getStepTradeStatus() {
        return stepTradeStatus;
    }

    /**
     * 设置åˆ†é˜¶æ®µä»˜æ¬¾çŠ¶æ€
     */
    public void setStepTradeStatus(String stepTradeStatus) {
        this.stepTradeStatus = stepTradeStatus == null ? null : stepTradeStatus.trim();
    }

    /**
     * 返回åˆ†é˜¶æ®µå·²ä»˜é‡‘é¢
     * @return åˆ†é˜¶æ®µå·²ä»˜é‡‘é¢
     */
    public Double getStepPaidFee() {
        return stepPaidFee;
    }

    /**
     * 设置åˆ†é˜¶æ®µå·²ä»˜é‡‘é¢
     */
    public void setStepPaidFee(Double stepPaidFee) {
        this.stepPaidFee = stepPaidFee;
    }

    /**
     * 返回æ˜¯å¦ç”Ÿæˆç»“ç®—æ¸…å• 0-å¦ 1-æ˜¯
     * @return æ˜¯å¦ç”Ÿæˆç»“ç®—æ¸…å• 0-å¦ 1-æ˜¯
     */
    public Boolean getIsClearing() {
        return isClearing;
    }

    /**
     * 设置æ˜¯å¦ç”Ÿæˆç»“ç®—æ¸…å• 0-å¦ 1-æ˜¯
     */
    public void setIsClearing(Boolean isClearing) {
        this.isClearing = isClearing;
    }

    /**
     * 返回è®¢å•å–æ¶ˆåŽŸå› 
     * @return è®¢å•å–æ¶ˆåŽŸå› 
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 设置è®¢å•å–æ¶ˆåŽŸå› 
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    /**
     * 返回æˆåŠŸï¼šSUCCESS  å¾…é€€æ¬¾ï¼šWAIT_REFUND  é»˜è®¤ï¼šNO_APPLY_CANCEL
     * @return æˆåŠŸï¼šSUCCESS  å¾…é€€æ¬¾ï¼šWAIT_REFUND  é»˜è®¤ï¼šNO_APPLY_CANCEL
     */
    public String getCancelStatus() {
        return cancelStatus;
    }

    /**
     * 设置æˆåŠŸï¼šSUCCESS  å¾…é€€æ¬¾ï¼šWAIT_REFUND  é»˜è®¤ï¼šNO_APPLY_CANCEL
     */
    public void setCancelStatus(String cancelStatus) {
        this.cancelStatus = cancelStatus == null ? null : cancelStatus.trim();
    }

    /**
     * 返回è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     * @return è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置è®¢å•çŠ¶æ€ WAIT_BUYER_PAY å·²ä¸‹å•ç­‰å¾…ä»˜æ¬¾ WAIT_SELLER_SEND-å·²ä»˜æ¬¾ç­‰å¾…å‘è´§ WAIT_BUYER_CONFIRM-å·²å‘è´§ç­‰å¾…ç¡®è®¤æ”¶è´§ WAIT_BUYER_SIGNED-å¾…è¯„ä»· TRADE_FINISHED-å·²å®Œæˆ TRADE_CLOSED_BY_REFUND-å·²å…³é—­(é€€æ¬¾å…³é—­è®¢å•) TRADE_CLOSED_BY_CANCEL-å·²å…³é—­(å–æ¶ˆå…³é—­è®¢å•)
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 返回æ˜¯å¦ä¸ºè™šæ‹Ÿè®¢å• 0-å¦ 1-æ˜¯
     * @return æ˜¯å¦ä¸ºè™šæ‹Ÿè®¢å• 0-å¦ 1-æ˜¯
     */
    public Boolean getIsVirtual() {
        return isVirtual;
    }

    /**
     * 设置æ˜¯å¦ä¸ºè™šæ‹Ÿè®¢å• 0-å¦ 1-æ˜¯
     */
    public void setIsVirtual(Boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

    /**
     * 返回ipåœ°å€
     * @return ipåœ°å€
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ipåœ°å€
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * 返回è®¢å•ç±»åž‹ï¼š0-æ™®é€šè®¢å• 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-æ‹¼å›¢ 4-å……å€¼
     * @return è®¢å•ç±»åž‹ï¼š0-æ™®é€šè®¢å• 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-æ‹¼å›¢ 4-å……å€¼
     */
    public Boolean getType() {
        return type;
    }

    /**
     * 设置è®¢å•ç±»åž‹ï¼š0-æ™®é€šè®¢å• 1-é›¶å…ƒè´­ 2-åˆ†æœŸè´­ 3-æ‹¼å›¢ 4-å……å€¼
     */
    public void setType(Boolean type) {
        this.type = type;
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
     * 返回åº—é“ºä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     * @return åº—é“ºä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     */
    public Double getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置åº—é“ºä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     */
    public void setDiscountCouponShop(Double discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回å¹³å°ä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     * @return å¹³å°ä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     */
    public Double getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置å¹³å°ä¼˜æƒ å·ä¼˜æƒ é‡‘é¢
     */
    public void setDiscountCouponPlatform(Double discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回è®¢å•ä¼˜æƒ æ€»é‡‘é¢
     * @return è®¢å•ä¼˜æƒ æ€»é‡‘é¢
     */
    public Double getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置è®¢å•ä¼˜æƒ æ€»é‡‘é¢
     */
    public void setDiscountFee(Double discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回é…é€ç±»åž‹
     * @return é…é€ç±»åž‹
     */
    public String getShippingType() {
        return shippingType;
    }

    /**
     * 设置é…é€ç±»åž‹
     */
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType == null ? null : shippingType.trim();
    }

    /**
     * 返回è®¢å•æ¥æºå¹³å° ç”µè„‘-pc æ‰‹æœºç½‘é¡µ-wap ç§»åŠ¨ç«¯-app
     * @return è®¢å•æ¥æºå¹³å° ç”µè„‘-pc æ‰‹æœºç½‘é¡µ-wap ç§»åŠ¨ç«¯-app
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置è®¢å•æ¥æºå¹³å° ç”µè„‘-pc æ‰‹æœºç½‘é¡µ-wap ç§»åŠ¨ç«¯-app
     */
    public void setPlatform(String platform) {
        this.platform = platform == null ? null : platform.trim();
    }

    /**
     * 返回ç”¨æˆ·è¯„ä»·çŠ¶æ€ 0-æœªè¯„ä»· 1-å·²è¯„ä»·
     * @return ç”¨æˆ·è¯„ä»·çŠ¶æ€ 0-æœªè¯„ä»· 1-å·²è¯„ä»·
     */
    public Boolean getRateStatus() {
        return rateStatus;
    }

    /**
     * 设置ç”¨æˆ·è¯„ä»·çŠ¶æ€ 0-æœªè¯„ä»· 1-å·²è¯„ä»·
     */
    public void setRateStatus(Boolean rateStatus) {
        this.rateStatus = rateStatus;
    }

    /**
     * 返回åº”ç”¨ä¼˜æƒ å·ç 
     * @return åº”ç”¨ä¼˜æƒ å·ç 
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * 设置åº”ç”¨ä¼˜æƒ å·ç 
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode == null ? null : couponCode.trim();
    }

    /**
     * 返回æ‹¼å›¢çŠ¶æ€ 
NO_APPLY ä¸åº”ç”¨æ‹¼å›¢
IN_PROCESSæ‹¼å›¢ä¸­ 
SUCCESS æˆåŠŸ 
FAILED å¤±è´¥
     * @return æ‹¼å›¢çŠ¶æ€ 
NO_APPLY ä¸åº”ç”¨æ‹¼å›¢
IN_PROCESSæ‹¼å›¢ä¸­ 
SUCCESS æˆåŠŸ 
FAILED å¤±è´¥
     */
    public String getGroupBuyStatus() {
        return groupBuyStatus;
    }

    /**
     * 设置æ‹¼å›¢çŠ¶æ€ 
NO_APPLY ä¸åº”ç”¨æ‹¼å›¢
IN_PROCESSæ‹¼å›¢ä¸­ 
SUCCESS æˆåŠŸ 
FAILED å¤±è´¥
     */
    public void setGroupBuyStatus(String groupBuyStatus) {
        this.groupBuyStatus = groupBuyStatus == null ? null : groupBuyStatus.trim();
    }

    /**
     * 返回æ˜¯å¦åˆ é™¤è®¢å• 0-æœªåˆ é™¤ 1-å·²åˆ é™¤
     * @return æ˜¯å¦åˆ é™¤è®¢å• 0-æœªåˆ é™¤ 1-å·²åˆ é™¤
     */
    public Boolean getIsDel() {
        return isDel;
    }

    /**
     * 设置æ˜¯å¦åˆ é™¤è®¢å• 0-æœªåˆ é™¤ 1-å·²åˆ é™¤
     */
    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
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

    public Integer getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Integer receiveTime) {
        this.receiveTime = receiveTime;
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

    public String getPayBillId() {
        return payBillId;
    }

    public void setPayBillId(String payBillId) {
        this.payBillId = payBillId == null ? null : payBillId.trim();
    }
}