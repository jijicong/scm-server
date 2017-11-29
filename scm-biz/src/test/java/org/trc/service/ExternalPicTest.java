package org.trc.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.goods.IExternalPictureBiz;

public class ExternalPicTest extends BaseTest{

    @Autowired
    private IExternalPictureBiz externalPictureBiz;

    @Test
    public void testPicUpload(){
        externalPictureBiz.uploadExternalPic();
    }

}
