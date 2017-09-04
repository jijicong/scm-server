package org.trc.domain.dict;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.CommonDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

@Table(name = "dict_type")
public class DictType extends CommonDO {
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @NotEmpty
    @Length(max = 32, message = "字典类型编码长度不能超过16个")
    private String code;
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "字典类型名称长度不能超过64个")
    private String name;
    @FormParam("description")
    @Length(max = 512, message = "字典类型说明长度不能超过512个")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

}