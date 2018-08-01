package org.trc.form.goods;

import io.swagger.annotations.Api;
import org.trc.domain.BaseDO;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;

import java.util.List;

/**
 * Created by hzgjl on 2018/7/31.
 */
@Api("商品组新增/编辑提交表单数据")
public class ItemGroupForm extends BaseDO {
    private static final long serialVersionUID = 3459115611247768853L;

   private ItemGroup itemGroup;

   private List<ItemGroupUser> groupUserList;

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public List<ItemGroupUser> getGroupUserList() {
        return groupUserList;
    }

    public void setGroupUserList(List<ItemGroupUser> groupUserList) {
        this.groupUserList = groupUserList;
    }
}
