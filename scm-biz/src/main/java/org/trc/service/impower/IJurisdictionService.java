package org.trc.service.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.Jurisdiction;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IJurisdictionService extends IBaseService<Jurisdiction, Long> {

    List<Jurisdiction> selectJurisdictionListByCodes(Long... codes) throws Exception;
}
