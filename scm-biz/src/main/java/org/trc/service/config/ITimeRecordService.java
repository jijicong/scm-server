package org.trc.service.config;

import org.trc.domain.config.TimeRecord;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzszy on 2017/7/6.
 */
public interface ITimeRecordService extends IBaseService<TimeRecord, Long> {
    List<TimeRecord> getLatestRecord() throws Exception;
}
