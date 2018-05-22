package org.trc.resource.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * 自营仓库
 * Created by hzcyn on 2018/5/22.
 */
@Component
@Path("wmsApi")
public class WmsApiResource {

    private Logger logger = LoggerFactory.getLogger(WmsApiResource.class);

}
