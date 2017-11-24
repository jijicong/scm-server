package org.trc.form.system;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * @author hzszy
 */
public class SellChannelFrom extends QueryModel {
    /**
     * 销售渠道名称
     */
    @QueryParam("sellName")
    @Length(max = 100, message = "销售渠道编码长度不能超过100")
    private String sellName;


    /**
     * 销售渠道编号
     */
    @QueryParam("sellCode")
    @Length(max = 32, message = "销售渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String sellCode;


    /**
     * 销售渠道类型
     */
    @QueryParam("sellType")
    @Length(max = 2, message = "销售渠道类型字母和数字不能超过2个,汉字不能超过1个")
    private String sellType;


    public String getSellName() {
        return sellName;
    }

    public void setSellName(String sellName) {
        this.sellName = sellName;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    @Override
    public String toString() {
        return  ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
