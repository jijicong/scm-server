package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.Certificate;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ICertificateService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("certificateService")
public class CertificateService extends BaseService<Certificate, Long> implements ICertificateService{
}
