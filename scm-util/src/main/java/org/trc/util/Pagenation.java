package org.trc.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/22.
 */
public class Pagenation<T> implements Serializable {
    /**
     * 每页最大记录条数
     */
    private static final int MAX_PAGE_SIZE = 100;

    // -- 分页参数 --//
    /*
    当前页数
     */
    @QueryParam("pageNo")
    protected int pageNo = 1;
    /*
    开始记录行数
     */
    @QueryParam("start")
    protected int start = 0;
    /*
    每页记录条数
     */
    @QueryParam("pageSize")
    protected int pageSize = 10;

    // -- 返回结果 --//
    protected List<T> result = new ArrayList<T>();
    protected long totalCount = -1;

    // -- 构造函数 --//
    public Pagenation() {

    }

    public Pagenation(int pageSize) {
        this.pageSize = pageSize;
    }

    // -- 分页参数访问函数 --//

    /**
     *获取记录开始行数
     */
    public int getStart() {
        return start;
    }
    /**
     *设置记录开始行数
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * 获得当前页的页号,序号从1开始,默认为1.
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(final int pageNo) {
        this.pageNo = pageNo;
        if (pageNo < 1) {
            this.pageNo = 1;
        }
    }

    /**
     * 返回Page对象自身的setPageNo函数,可用于连续设置。
     */
    public Pagenation<T> pageNo(final int thePageNo) {
        setPageNo(thePageNo);
        return this;
    }

    /**
     * 获得每页的记录数量, 默认为-1.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量.
     */
    public void setPageSize(final int pageSize) {
        if(pageSize < 0)
            this.pageSize = -1;
        else if(pageSize >= MAX_PAGE_SIZE)
            this.pageSize = MAX_PAGE_SIZE;
        else
            this.pageSize = pageSize;
    }

    /**
     * 返回Page对象自身的setPageSize函数,可用于连续设置。
     */
    public Pagenation<T> pageSize(final int thePageSize) {
        setPageSize(thePageSize);
        return this;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
     */
    public int getFirst() {
        return ((pageNo - 1) * pageSize) + 1;
    }

    /**
     * 获得页内的记录列表.
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setResult(final List<T> result) {
        this.result = result;
    }

    /**
     * 获得总记录数, 默认值为-1.
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数.
     */
    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        if (totalCount < 0) {
            return -1;
        }
        long count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean isHasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (isHasNext()) {
            return pageNo + 1;
        } else {
            return pageNo;
        }
    }

    /**
     * 是否还有上一页.
     */
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (isHasPre()) {
            return pageNo - 1;
        } else {
            return pageNo;
        }
    }

}

