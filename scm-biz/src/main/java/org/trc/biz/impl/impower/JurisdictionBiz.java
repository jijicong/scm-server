package org.trc.biz.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.biz.impower.IJurisdictionBiz;
import org.trc.domain.impower.Jurisdiction;
import org.trc.service.impower.IJurisdictionService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service
public class JurisdictionBiz implements IJurisdictionBiz {

    @Resource
    private IJurisdictionService jurisdictionService;

    private final static Integer WHOLE_JURISDICTION_ID=1;//全局角色的所属

    private final static Integer CHANNEL_JURISDICTION_ID=2;//渠道角色的所属

    @Override
    public List<Jurisdiction> findWholeJurisdiction() throws Exception {
        Jurisdiction jurisdiction=new Jurisdiction();
        jurisdiction.setBelong(WHOLE_JURISDICTION_ID);
        return jurisdictionService.select(jurisdiction);
    }

    @Override
    public List<Jurisdiction> findChannelJurisdiction() throws Exception {
        Jurisdiction jurisdiction=new Jurisdiction();
        jurisdiction.setBelong(CHANNEL_JURISDICTION_ID);
        return jurisdictionService.select(jurisdiction);
    }
}
