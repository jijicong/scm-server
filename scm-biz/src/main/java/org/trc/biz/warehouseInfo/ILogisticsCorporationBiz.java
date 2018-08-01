package org.trc.biz.warehouseInfo;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.LogisticsCorporation;
import org.trc.form.warehouseInfo.LogisticsCorporationForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzcyn on 2018/5/3.
 */
public interface ILogisticsCorporationBiz {

    /**
     * 物流公司信息分页查询
     *
     * @param query 查询条件
     * @param page  分页
     * @return
     */
    Pagenation<LogisticsCorporation> selectLogisticsCorporationByPage(LogisticsCorporationForm query, Pagenation<LogisticsCorporation> page);

    Response saveLogisticsCorporation(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo);

    void updateLogisticsCorporation(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo);

    void updateLogisticsCorporationState(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 获取货运企业
     * @return
     */
    List<LogisticsCorporation> findEnabled();
}
