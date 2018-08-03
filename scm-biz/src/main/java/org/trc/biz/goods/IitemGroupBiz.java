package org.trc.biz.goods;

import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.goods.ItemGroupForm;
import org.trc.form.goods.ItemGroupQuery;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzgjl on 2018/7/26.
 */
public interface IitemGroupBiz  {
    Pagenation<ItemGroup> itemGroupPage(ItemGroupQuery itemGroupQuery, Pagenation<ItemGroup> page, AclUserAccreditInfo aclUserAccreditInfo);

    ItemGroup queryDetailByCode(String itemGroupCode);

    void editDetail(ItemGroupForm form,AclUserAccreditInfo aclUserAccreditInfo);

    void itemGroupSave(ItemGroupForm form, AclUserAccreditInfo aclUserAccreditInfo);

    ItemGroup findItemGroupByName(String name);

    void updateStatus(String isValid, String itemGroupCode,AclUserAccreditInfo aclUserAccreditInfo);

    List<ItemGroupUser> queryItemGroupUserListByCode(String itemGroupCode);
}
