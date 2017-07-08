package org.trc.custom;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 金额分转元
 * Created by hzwdx on 2017/6/20.
 */
public class MoneySerializer extends JsonSerializer<Long> {

    public final static int PERCENT = 100;

    public final static String TIP = "0.00";

    @Override
    public void serialize(Long s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(null == s){
            s = 0L;
        }
        DecimalFormat df = new DecimalFormat(TIP);
        String rel = df.format((float)s/PERCENT);
        jsonGenerator.writeNumber(rel);
    }

}
