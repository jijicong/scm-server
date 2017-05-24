package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/5/23 0023.
 */
public class OrderDO {
    //授权时获取的ACCESS TOKEN
    private String token;

    //第三方的订单单号
    private String thirdOrder;

    //商品SKU信息
    private String sku;

    //收货人
    private String name;

    //一级地址
    private String province;

    //二级地址
    private String city;

    //三级地址
    private String county;

    //四级地址
    private String town;

    //详细地址
    private String address;

    //邮编
    private String zip;

    //座机号
    private String phone;

    //手机号
    private String mobile;

    //邮箱
    private String email;

    //备注
    private String remark;

    //开票方式 1为随货开票，0为订单预借，2为集中开票
    private int invoiceState;

    //1普通发票 2增值税发票
    private int invoiceType;

    //4 个人，5单位
    private int selectedInvoiceTile;

    //发票抬头 (如果selectedInvoiceTitle=5则此字段必须)
    private String companyName;

    //1:明细，3：电脑配件，19:耗材，22：办公用品
    private int invoiceContent;

    //付款方式 1：货到付款，2：邮局付款，4：在线支付，5：公司转账，6：银行转账，7：网银钱包，101：金采支付
    private int paymentType;

    //使用余额paymentType=4时，此值固定是1 其他支付方式0
    private int isUseBalance;

    //是否预占库存 0是预占库存（需要调用确认订单接口），1是不预占库存  金融支付必须预占库存传0
    private int submitState;

    //增值税收票人姓名
    private String invoiceName;

    //增值票收票人电话 当invoiceType=2 且invoiceState=1时则此字段必填
    private String invoicePhone;

    //增值票收票人所在省(京东地址编码) 当invoiceType=2 且invoiceState=1时则此字段必填
    private int invoiceProvice;

    //增值票收票人所在市(京东地址编码)
    private int invoiceCity;

    //增值票收票人所在区/县(京东地址编码)
    private int invoiceCounty;

    //增值票收票人所在地址
    private String invoiceAddress;

    //下单价格模式
    private int doOrderPriceMode;

    //客户端订单价格快照
    private String orderPriceSnap;

    //大家电配送日期 默认值为-1，0表示当天，1表示明天，2：表示后天; 如果为-1表示不使用大家电预约日历
    private int reservingDate;

    //大家电安装日期 不支持默认按-1处理，0表示当天，1表示明天，2：表示后天
    private int installDate;

    //大家电是否选择了安装  是否选择了安装，默认为true，选择了“暂缓安装”，此为必填项，必填值为false。
    private boolean needInstall;

    //中小件配送预约日期 格式：yyyy-MM-dd
    private String promiseDate;

    //中小件配送预约时间段 时间段如： 9:00-15:00
    private String promiseTimeRange;

    //中小件预约时间段的标记
    private Integer promiseTimeRangeCode;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getThirdOrder() {
        return thirdOrder;
    }

    public void setThirdOrder(String thirdOrder) {
        this.thirdOrder = thirdOrder;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getInvoiceState() {
        return invoiceState;
    }

    public void setInvoiceState(int invoiceState) {
        this.invoiceState = invoiceState;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public int getSelectedInvoiceTile() {
        return selectedInvoiceTile;
    }

    public void setSelectedInvoiceTile(int selectedInvoiceTile) {
        this.selectedInvoiceTile = selectedInvoiceTile;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(int invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getIsUseBalance() {
        return isUseBalance;
    }

    public void setIsUseBalance(int isUseBalance) {
        this.isUseBalance = isUseBalance;
    }

    public int getSubmitState() {
        return submitState;
    }

    public void setSubmitState(int submitState) {
        this.submitState = submitState;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getInvoicePhone() {
        return invoicePhone;
    }

    public void setInvoicePhone(String invoicePhone) {
        this.invoicePhone = invoicePhone;
    }

    public int getInvoiceProvice() {
        return invoiceProvice;
    }

    public void setInvoiceProvice(int invoiceProvice) {
        this.invoiceProvice = invoiceProvice;
    }

    public int getInvoiceCity() {
        return invoiceCity;
    }

    public void setInvoiceCity(int invoiceCity) {
        this.invoiceCity = invoiceCity;
    }

    public int getInvoiceCounty() {
        return invoiceCounty;
    }

    public void setInvoiceCounty(int invoiceCounty) {
        this.invoiceCounty = invoiceCounty;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public int getDoOrderPriceMode() {
        return doOrderPriceMode;
    }

    public void setDoOrderPriceMode(int doOrderPriceMode) {
        this.doOrderPriceMode = doOrderPriceMode;
    }

    public String getOrderPriceSnap() {
        return orderPriceSnap;
    }

    public void setOrderPriceSnap(String orderPriceSnap) {
        this.orderPriceSnap = orderPriceSnap;
    }

    public int getReservingDate() {
        return reservingDate;
    }

    public void setReservingDate(int reservingDate) {
        this.reservingDate = reservingDate;
    }

    public int getInstallDate() {
        return installDate;
    }

    public void setInstallDate(int installDate) {
        this.installDate = installDate;
    }

    public boolean isNeedInstall() {
        return needInstall;
    }

    public void setNeedInstall(boolean needInstall) {
        this.needInstall = needInstall;
    }

    public String getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(String promiseDate) {
        this.promiseDate = promiseDate;
    }

    public String getPromiseTimeRange() {
        return promiseTimeRange;
    }

    public void setPromiseTimeRange(String promiseTimeRange) {
        this.promiseTimeRange = promiseTimeRange;
    }

    public Integer getPromiseTimeRangeCode() {
        return promiseTimeRangeCode;
    }

    public void setPromiseTimeRangeCode(Integer promiseTimeRangeCode) {
        this.promiseTimeRangeCode = promiseTimeRangeCode;
    }
}
