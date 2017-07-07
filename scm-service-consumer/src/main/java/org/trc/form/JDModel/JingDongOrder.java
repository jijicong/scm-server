package org.trc.form.JDModel;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/1.
 */
public class JingDongOrder {

    //仓库订单编码
    private String warehouseOrderCode;
    //第三方的订单单号
    private String thirdOrder;
    //商品sku列表
    private List<JdSku> sku;
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
    //备注（少于100字）
    private String remark;
    //开票方式(1-为随货开票，0-为订单预借，2-为集中开票 )
    private Integer invoiceState;
    //发票类型：1-普通发票, 2-增值税发票
    private Integer invoiceType;
    //发票类型：4个人，5单位
    private Integer selectedInvoiceTitle;
    //发票抬头
    private String companyName;
    //1:明细，3：电脑配件，19:耗材，22：办公用品
    private Integer invoiceContent;
    //支付方式 (1：货到付款，2：邮局付款，4：在线支付，5：公司转账，6：银行转账，7：网银钱包，101：金采支付)
    private Integer paymentType;
    //使用余额
    private Integer isUseBalance;
    //是否预占库存，0是预占库存（需要调用确认订单接口），1是不预占库存     金融支付必须预占库存传0
    private Integer submitState;
    //增值票收票人姓名
    private String invoiceName;
    //增值票收票人电话
    private String invoicePhone;
    //增值票收票人所在省(京东地址编码)
    private Integer invoiceProvice;
    //增值票收票人所在市(京东地址编码)
    private Integer invoiceCity;
    //增值票收票人所在区/县(京东地址编码)
    private Integer invoiceCounty;
    //增值票收票人所在地址
    private String invoiceAddress;
    //下单价格模式
    private Integer doOrderPriceMode;
    //客户端订单价格快照
    private List<OrderPriceSnap> orderPriceSnap;
    //大家电配送日期,默认值为-1，0表示当天，1表示明天，2：表示后天; 如果为-1表示不使用大家电预约日历
    private Integer reservingDate;
    //大家电安装日期,不支持默认按-1处理，0表示当天，1表示明天，2：表示后天
    private Integer installDate;
    //大家电是否选择了安装,是否选择了安装，默认为true，选择了“暂缓安装”，此为必填项，必填值为false
    private Boolean needInstall;
    //中小件配送预约日期,格式：yyyy-MM-dd
    private String promiseDate;
    //中小件配送预约时间段,时间段如： 9:00-15:00
    private String promiseTimeRange;
    //中小件预约时间段的标记
    private Integer promiseTimeRangeCode;

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getThirdOrder() {
        return thirdOrder;
    }

    public void setThirdOrder(String thirdOrder) {
        this.thirdOrder = thirdOrder;
    }

    public List<JdSku> getSku() {
        return sku;
    }

    public void setSku(List<JdSku> sku) {
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

    public Integer getInvoiceState() {
        return invoiceState;
    }

    public void setInvoiceState(Integer invoiceState) {
        this.invoiceState = invoiceState;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Integer getSelectedInvoiceTitle() {
        return selectedInvoiceTitle;
    }

    public void setSelectedInvoiceTitle(Integer selectedInvoiceTitle) {
        this.selectedInvoiceTitle = selectedInvoiceTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(Integer invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getIsUseBalance() {
        return isUseBalance;
    }

    public void setIsUseBalance(Integer isUseBalance) {
        this.isUseBalance = isUseBalance;
    }

    public Integer getSubmitState() {
        return submitState;
    }

    public void setSubmitState(Integer submitState) {
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

    public List<OrderPriceSnap> getOrderPriceSnap() {
        return orderPriceSnap;
    }

    public void setOrderPriceSnap(List<OrderPriceSnap> orderPriceSnap) {
        this.orderPriceSnap = orderPriceSnap;
    }

    public void setInvoicePhone(String invoicePhone) {
        this.invoicePhone = invoicePhone;
    }

    public Integer getInvoiceProvice() {
        return invoiceProvice;
    }

    public void setInvoiceProvice(Integer invoiceProvice) {
        this.invoiceProvice = invoiceProvice;
    }

    public Integer getInvoiceCity() {
        return invoiceCity;
    }

    public void setInvoiceCity(Integer invoiceCity) {
        this.invoiceCity = invoiceCity;
    }

    public Integer getInvoiceCounty() {
        return invoiceCounty;
    }

    public void setInvoiceCounty(Integer invoiceCounty) {
        this.invoiceCounty = invoiceCounty;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public Integer getDoOrderPriceMode() {
        return doOrderPriceMode;
    }

    public void setDoOrderPriceMode(Integer doOrderPriceMode) {
        this.doOrderPriceMode = doOrderPriceMode;
    }

    public Integer getReservingDate() {
        return reservingDate;
    }

    public void setReservingDate(Integer reservingDate) {
        this.reservingDate = reservingDate;
    }

    public Integer getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Integer installDate) {
        this.installDate = installDate;
    }

    public Boolean getNeedInstall() {
        return needInstall;
    }

    public void setNeedInstall(Boolean needInstall) {
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
