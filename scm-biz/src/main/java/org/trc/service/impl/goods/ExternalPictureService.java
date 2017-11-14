package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ExternalPicture;
import org.trc.service.goods.IExternalPictureService;
import org.trc.service.impl.BaseService;

@Service("externalPictureService")
public class ExternalPictureService extends BaseService<ExternalPicture, Long> implements IExternalPictureService {
}
