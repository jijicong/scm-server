package org.trc.custom;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created by hzwdx on 2017/6/20.
 */
public class MoneySerializer extends JsonSerializer<Long> {

    public final static int PERCENT = 100;

    @Override
    public void serialize(Long s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(null == s){
            s = 0L;
        }
        jsonGenerator.writeNumber(s/PERCENT);
    }

}
