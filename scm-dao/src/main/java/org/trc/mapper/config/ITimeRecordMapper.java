package org.trc.mapper.config;

import org.trc.domain.config.TimeRecord;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzszy on 2017/7/6.
 */
public interface ITimeRecordMapper extends BaseMapper<TimeRecord> {
    List<TimeRecord> getLatestRecord() throws Exception;
}
