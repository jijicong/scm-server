package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.config.LogInfo;
import org.trc.form.config.LogInfoForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzqph on 2017/7/17.
 */
@Component
@Path(SupplyConstants.LogInfo.LOG_INFO_PAGE)
public class LogInfoResource {
    @Autowired
    private LogInfoBiz logInfoBiz;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response logInfoPage(@BeanParam LogInfoForm form, @BeanParam Pagenation<LogInfo> page) {
        return ResultUtil.createSuccessPageResult(logInfoBiz.logInfoPage(form, page));
    }
}
