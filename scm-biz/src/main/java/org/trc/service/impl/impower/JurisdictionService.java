package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.Jurisdiction;
import org.trc.mapper.impower.JurisdictionMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IJurisdictionService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("jurisdictionService")
public class JurisdictionService extends BaseService<Jurisdiction, Long> implements IJurisdictionService {

    @Autowired
    private JurisdictionMapper jurisdictionMapper;

    @Override
    public List<Jurisdiction> selectJurisdictionListByCodes(Long... codes) throws Exception {
        return jurisdictionMapper.selectJurisdictionListByCodes(codes);
    }
}
