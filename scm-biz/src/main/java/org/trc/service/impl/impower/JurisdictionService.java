package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.Jurisdiction;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IJurisdictionService;

/**
 * Created by sone on 2017/5/11.
 */
@Service("jurisdictionService")
public class JurisdictionService extends BaseService<Jurisdiction,Long> implements IJurisdictionService{
}
