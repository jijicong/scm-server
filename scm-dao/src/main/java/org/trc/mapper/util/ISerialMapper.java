package org.trc.mapper.util;

import org.apache.ibatis.annotations.*;
import org.trc.domain.util.Serial;
import org.trc.util.BaseMapper;

/**
 * Created by sone on 2017/5/8.
 */
public interface ISerialMapper extends BaseMapper<Serial> {
    //查询正确的流水号
    int selectNumber(String name);
    //根据名字修改版本号
    @Update("update serial set number=#{number} WHERE name=#{name} and number=#{originalNumber}")
    int updateSerialVersionByName(@Param("name") String name, @Param("number") int number, @Param("originalNumber") int originalNumber);

}
