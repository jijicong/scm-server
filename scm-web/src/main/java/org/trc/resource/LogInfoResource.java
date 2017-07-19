package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.config.LogInfo;
import org.trc.form.category.BrandForm;
import org.trc.form.config.LogInfoForm;
import org.trc.service.impl.config.LogInfoService;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public Pagenation<LogInfo> logInfoPage(@BeanParam LogInfoForm form, @BeanParam Pagenation<LogInfo> page){
        return logInfoBiz.logInfoPage(form,page);
    }
}
