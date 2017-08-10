package org.trc.domain.config;

import java.util.List;

/**
 * Created by hzwyz on 2017/8/10 0010.
 */
public class QureyCondition {
    private List<String> list;

    private Long id;

    private String type;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
