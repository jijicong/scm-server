package org.trc.util;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class PageResult<T>{

    /**
     * 总记录条数
     */
    private int results;
    /**
     * 记录结果集
     */
    private List<T> rows;

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
