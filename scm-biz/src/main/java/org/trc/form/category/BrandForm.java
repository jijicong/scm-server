package org.trc.form.category;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by hzqph on 2017/4/27.
 */
public class BrandForm extends QueryModel {
    @QueryParam("name")
    private String name;
    @QueryParam("alise")
    private String alise;
    @QueryParam("brandCode")
    private String brandCode;
    @QueryParam("startUpdateTime")
    private String startUpdateTime;
    @QueryParam("endUpdateTime")
    private String endUpdateTime;
    @QueryParam("pageIds")
    private String pageIds;


    public String getAlise() {
        return alise;
    }

    public void setAlise(String alise) {
        this.alise = alise;
    }


    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

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

    public String getPageIds() {
        return pageIds;
    }

    public void setPageIds(String pageIds) {
        this.pageIds = pageIds;
    }
}
