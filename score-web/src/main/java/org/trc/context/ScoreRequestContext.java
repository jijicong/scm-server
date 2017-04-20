package org.trc.context;

import com.tairanchina.beego.api.model.BeegoToken;

/**
 * Created by george on 2017/4/7.
 */
public class ScoreRequestContext {

    private BeegoToken beegoToken;

    public BeegoToken getBeegoToken() {
        return beegoToken;
    }

    public void setBeegoToken(BeegoToken beegoToken) {
        this.beegoToken = beegoToken;
    }
}
