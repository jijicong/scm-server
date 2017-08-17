package org.trc.custom;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.trc.util.CommonUtil;

import java.io.IOException;

/**
 * Created by hzwdx on 2017/8/17.
 */
public class WeightSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(null == s){
            s = 0L;
        }
        String rel = CommonUtil.getWeight(s).toPlainString();
        jsonGenerator.writeNumber(rel);
    }
}
