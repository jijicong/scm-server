package org.trc.form.goods;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;
import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/1.
 */
public class SkusForm extends QueryModel {

    @QueryParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }
}
