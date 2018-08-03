package org.trc.form.goods;

import io.swagger.annotations.Api;
import lombok.Data;

import javax.ws.rs.FormParam;
import java.util.List;

/**
 * Created by hzgjl on 2018/8/3.
 */
@Data
@Api("商品组编辑传值")
public class ItemGroupForm2 {

    //@FormParam("itemGroup")
    private ItemGroupVo itemGroup;

   // @FormParam("groupUserList")
    private List<ItemGroupUserVO> groupUserList;

}
