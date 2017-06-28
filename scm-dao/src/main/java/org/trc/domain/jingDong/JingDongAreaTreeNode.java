package org.trc.domain.jingDong;

import java.util.List;

/**
 * Created by sone on 2017/6/19.
 */
public class JingDongAreaTreeNode {

    private Long id; //这里的id相当于Location中的code
    private String text;//Location中的 province city district
    private String areaCode;
    private String jdCode;
    private boolean isleaf;
    private List<JingDongAreaTreeNode> children;//子节点

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getJdCode() {
        return jdCode;
    }

    public void setJdCode(String jdCode) {
        this.jdCode = jdCode;
    }

    public boolean isIsleaf() {
        return isleaf;
    }

    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }

    public List<JingDongAreaTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<JingDongAreaTreeNode> children) {
        this.children = children;
    }

}
