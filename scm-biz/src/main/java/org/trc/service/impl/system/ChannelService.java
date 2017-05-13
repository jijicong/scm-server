package org.trc.service.impl.system;

import org.springframework.stereotype.Service;
import org.trc.domain.System.Channel;
import org.trc.service.System.IChannelService;
import org.trc.service.impl.BaseService;

/**
 * Created by sone on 2017/5/2.
 */
@Service("channelService")
public class ChannelService extends BaseService<Channel,Long> implements IChannelService{
}
