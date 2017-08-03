package org.trc.service.impl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.config.TimeRecord;
import org.trc.mapper.config.ITimeRecordMapper;
import org.trc.service.config.ITimeRecordService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzszy on 2017/7/6.
 */
@Service("timeRecordService")
public class TimeRecordService extends BaseService<TimeRecord,Long> implements ITimeRecordService {

    @Autowired
    ITimeRecordMapper timeRecordMapper;

    public List<TimeRecord> getLatestRecord() throws Exception{
        return timeRecordMapper.getLatestRecord();
    }
}
