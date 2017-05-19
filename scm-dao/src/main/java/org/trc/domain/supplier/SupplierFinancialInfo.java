package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import java.util.Date;

public class SupplierFinancialInfo extends ScmDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("supplierCode")
    //@NotEmpty
    @Length(max = 32, message = "供应链编号长度不能超过32个")
    private String supplierCode;
    @FormParam("depositBank")
    @NotEmpty
    @Length(max = 128, message = "开户行名称长度不能超过128个")
    private String depositBank;
    @FormParam("bankAccount")
    @NotEmpty
    @Length(max = 32, message = "银行账户长度不能超过32个")
    private String bankAccount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank == null ? null : depositBank.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

}