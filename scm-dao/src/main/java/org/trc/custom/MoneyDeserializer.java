package org.trc.custom;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 金额元转分
 * Created by hzwdx on 2017/6/20.
 */
public class MoneyDeserializer extends JsonDeserializer<Long> {

    private final static BigDecimal PERCENT = new BigDecimal(100);

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if(StringUtils.isBlank(jsonParser.getText())){
            return 0L;
        }
        else{
            BigDecimal bigDecimal = new BigDecimal(jsonParser.getText());
            return bigDecimal.multiply(PERCENT).longValue();
        }
    }
}
