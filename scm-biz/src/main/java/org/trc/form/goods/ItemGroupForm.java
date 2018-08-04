package org.trc.form.goods;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;

import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by hzgjl on 2018/7/31.
 */
@Api("商品组新增提交表单数据")
@Data
public class ItemGroupForm implements Serializable{

    private static final long serialVersionUID = -196159265196008620L;

    private ItemGroup itemGroup;

    private List<ItemGroupUser> groupUserList;

}
