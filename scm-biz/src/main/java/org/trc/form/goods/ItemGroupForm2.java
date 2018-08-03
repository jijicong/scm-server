package org.trc.form.goods;

import io.swagger.annotations.Api;
import lombok.Data;

import java.util.List;

/**
 * Created by hzgjl on 2018/8/3.
 */
@Data
@Api("商品组编辑传值")
public class ItemGroupForm2 {

    private ItemGroupVo itemGroup;

    private List<ItemGroupUserVO> groupUserList;

}
