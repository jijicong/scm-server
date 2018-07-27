package org.trc.biz.goods;

import org.trc.domain.goods.ItemGroup;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.goods.ItemGroupForm;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import java.util.List;

/**
 * Created by hzgjl on 2018/7/26.
 */
public interface IitemGroupBiz  {
    Pagenation itemGroupPage(ItemGroupForm form,  Pagenation<ItemGroup> page,AclUserAccreditInfo aclUserAccreditInfo);

    ItemGroup queryDetailByCode(String itemGroupCode);

    void editDetail(ItemGroup itemGroup);

    void itemGroupSave(ItemGroup itemGroup);
}
