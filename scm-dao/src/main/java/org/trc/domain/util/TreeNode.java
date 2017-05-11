package org.trc.domain.util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sone on 2017/5/8.
 */
public class TreeNode implements Serializable{
    /*{"id" : "1","text":"山东","children":[
        {"id":"11","text":"济南","leaf":false},
        {"id":"12","text":"淄博","leaf":false,"children":[
            {"id":"121","text":"高青","leaf":true}*/
    private String id; //这里的id相当于Location中的code
    private String text;//Location中的 province city district
    private boolean isleaf;
    private List<TreeNode> children;//子节点

    public boolean isIsleaf() {
        return isleaf;
    }

    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", children=" + children +
                '}';
    }
}
