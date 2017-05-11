package org.trc.form.category;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by hzqph on 2017/4/27.
 */
public class BrandForm extends QueryModel{
     @QueryParam("name")
     private String name;
     @QueryParam("startUpdateTime")
     private String startUpdateTime;
     @QueryParam("endUpdateTime")
     private String endUpdateTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartUpdateTime() {
        return startUpdateTime;
    }

    public void setStartUpdateTime(String startUpdateTime) {
        this.startUpdateTime = startUpdateTime;
    }

    public String getEndUpdateTime() {
        return endUpdateTime;
    }

    public void setEndUpdateTime(String endUpdateTime) {
        this.endUpdateTime = endUpdateTime;
    }
}
