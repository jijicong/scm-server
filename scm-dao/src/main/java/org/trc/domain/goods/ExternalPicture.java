package org.trc.domain.goods;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

@Table(name = "external_picture")
public class ExternalPicture  implements Serializable {

    // 主键
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //供应商编号
    private String supplierCode;

    //商品SKU编号
    private String skuCode;

    //状态:1-成功,0-失败
    private String supplierSkuCode;

    //供应商编号
    private String status;

    //资源url路径
    private String url;

    //文件七牛存储路径
    private String filePath;

    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    // 最后更新时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
}
