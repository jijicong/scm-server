package org.trc.service.impl.system;

import org.springframework.stereotype.Service;
import org.trc.domain.System.ChannelSellChannel;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.impl.BaseService;

/**
 * @author hzszy
 */
@Service("channelSellChannelService")
public class ChannelSellChannelService extends BaseService<ChannelSellChannel,Long> implements IChannelSellChannelService {
}
