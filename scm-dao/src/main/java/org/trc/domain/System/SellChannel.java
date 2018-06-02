package org.trc.domain.System;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * @author hzszy
 */
@Setter
@Getter
public class SellChannel extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("sellCode")
    @Length(max = 32, message = "销售渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String sellCode;

    @FormParam("sellName")
    @Length(max = 100, message = "销售渠道编码长度不能超过100")
    private String sellName;


    @FormParam("sellType")
    @Length(max = 2, message = "销售渠道编码字母和数字不能超过2个,汉字不能超过1个")
    private String sellType;

    @FormParam("remark")
    @Length(max = 300, message = "销售渠道备注长度不能超过300")
    private String remark;

    @FormParam("storeId")
    @Length(max = 255, message = "门店ID不能超过255")
    private String storeId;

}
