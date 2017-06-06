package org.trc.mapper.system;

import org.trc.domain.System.Warehouse;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * warehouse Mapper
 * Created by sone on 2017/5/4.
 */
public interface IWarehouseMapper extends BaseMapper<Warehouse>{

    List<Warehouse> selectWarehouseNames(String strs[]);

}
