package org.trc.domain.taxrate;

import lombok.Data;
import org.trc.domain.util.CommonDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import java.math.BigDecimal;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/6/28
 */
@Data
public class TaxRate extends CommonDO {

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //税率code
    @FormParam("taxRateCode")
    private String taxRateCode;

    //税率
    @FormParam("taxrate")
    private BigDecimal taxRate;

    //描述
    @FormParam("remark")
    private String remark;
}
