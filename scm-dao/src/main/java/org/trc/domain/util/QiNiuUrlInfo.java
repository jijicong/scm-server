package org.trc.domain.util;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzcyn on 2018/9/4.
 */
@Setter
@Getter
public class QiNiuUrlInfo implements Serializable {
    @Id
    private Long id;
    private String code;
    private String url;
    private String isDeleted;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

}
