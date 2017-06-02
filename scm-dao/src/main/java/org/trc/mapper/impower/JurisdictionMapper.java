package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.Jurisdiction;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface JurisdictionMapper extends BaseMapper<Jurisdiction>{
        List<Jurisdiction> selectJurisdictionListByCodes(@Param("codes")Long ...code) throws Exception;
}