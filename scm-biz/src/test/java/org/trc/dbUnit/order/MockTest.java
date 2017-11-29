package org.trc.dbUnit.order;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class MockTest {

    @Test
    public void mock1(){
        List<String> list = mock(List.class);
        when(list.get(0)).thenReturn("helloworld");
        String result = list.get(0);
        //验证方法调用(是否调用了get(0))
        verify(list).get(0);
        //junit测试
        Assert.assertEquals("helloworld2", result);
    }

}
