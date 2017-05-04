package org.trc.form;

import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by hzwdx on 2017/5/3.
 */
@Component
public class QinniuUploadManager {

    public QinniuUploadManager(){
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());

    }

}
