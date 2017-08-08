package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.system.IMetadataBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.util.AppResult;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Created by hzwdx on 2017/8/7.
 */
@Component
@Path(SupplyConstants.Metadata.ROOT)
public class MetadataResource {

    @Autowired
    private IMetadataBiz metadataBiz;

    @GET
    @Path(SupplyConstants.Metadata.DICT)
    @Produces("application/json;charset=utf-8")
    public List<Dict> queryDict(){
        return metadataBiz.queryDict();
    }

    @GET
    @Path(SupplyConstants.Metadata.ADDRESS)
    @Produces("application/json;charset=utf-8")
    public List<AreaTreeNode> queryAddress(){
        return metadataBiz.queryAddress();
    }

    @GET
    @Path(SupplyConstants.Metadata.JD_ADDRESS)
    @Produces("application/json;charset=utf-8")
    public List<AreaTreeNode> queryJdAddress(){
        return metadataBiz.queryJDAddress();
    }


    @POST
    @Path(SupplyConstants.Metadata.JD_ADDRESS_UPDATE)
    @Produces("application/json;charset=utf-8")
    public AppResult jdAddressUpdate(){
        return metadataBiz.jDAddressUpdate();
    }



}
