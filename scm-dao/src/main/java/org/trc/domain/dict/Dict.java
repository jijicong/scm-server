package org.trc.domain.dict;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.util.CommonDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

//@Table(name = "dict")
//@NameStyle(Style.normal)
public class Dict extends CommonDO {
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("typeCode")
    @NotEmpty
    @Length(max = 32, message = "字典类型编码字母和数字不能超过32个,汉字不能超过16个")
    private String typeCode;
    @NotEmpty
    @Length(max = 64, message = "字典名称字母和数字不能超过64个,汉字不能超过32个")
    @FormParam("name")
    private String name;
    @FormParam("value")
    @NotEmpty
    @Length(max = 64, message = "字典值字母和数字不能超过64个,汉字不能超过32个")
    private String value;

    @Transient
    private String typeName;//字典类型名称 b

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}