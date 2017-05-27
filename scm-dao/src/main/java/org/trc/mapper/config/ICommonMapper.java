package org.trc.mapper.config;

import org.trc.domain.config.Common;
import org.trc.util.BaseMapper;

public interface ICommonMapper extends BaseMapper<Common> {
    Common selectByCode(String code);
}