package org.trc.domain.score;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.util.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;

//@Table(name = "dict")
//@NameStyle(Style.normal)
public class Dict extends BaseDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("typeNo")
    @NotEmpty
    @Length(max = 32, message = "字典类型编码字母和数字不能超过32个,汉字不能超过16个")
    private String typeNo;
    @NotEmpty
    @Length(max = 64, message = "字典名称字母和数字不能超过64个,汉字不能超过32个")
    @FormParam("name")
    private String name;
    @FormParam("value")
    @NotEmpty
    @Length(max = 64, message = "字典值字母和数字不能超过64个,汉字不能超过32个")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo == null ? null : typeNo.trim();
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

}