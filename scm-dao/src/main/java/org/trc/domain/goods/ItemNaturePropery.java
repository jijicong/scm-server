package org.trc.domain.goods;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class ItemNaturePropery extends ScmDO{
    private static final long serialVersionUID = 7225076807216077978L;

    @ApiParam(value = "主键ID")
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ApiParam(value = "商品ID")
    @FormParam("itemId")
    private Long itemId;
    @ApiParam(value = "商品SPU编号")
    @FormParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @ApiParam(value = "属性ID")
    @FormParam("propertyId")
    private Long propertyId;
    @ApiParam(value = "属性名称")
    @Transient
    private String propertyName;
    @ApiParam(value = "属性值ID")
    @FormParam("propertyValueId")
    private Long propertyValueId;
    @ApiParam(value = "是否有效:0-否,1-是")
    @FormParam("是否有效:0-否,1-是")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

    @ApiParam(value = "属性值")
    @Transient
    private String propertyValue;

    /**
     * 自然属性信息
     */
    @ApiParam(value = "[\n" +
            "    {\n" +
            "        \"propertyId\":\"属性ID\",\n" +
            "        \"propertyValue\":\"属性名称\"\n" +
            "        \"propertyValueId\":\"属性值ID\",\n" +
            "    }\n" +
            "]")
    @FormParam("naturePropertys")
    @Transient
    private String naturePropertys;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(Long propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public String getNaturePropertys() {
        return naturePropertys;
    }

    public void setNaturePropertys(String naturePropertys) {
        this.naturePropertys = naturePropertys;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}