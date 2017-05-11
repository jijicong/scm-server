package org.trc.form.system;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/8.
 */
public class AreaTreeNode {

    private String id;

    private String text;

    private boolean leaf;

    private List<AreaTreeNode> children;

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

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<AreaTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<AreaTreeNode> children) {
        this.children = children;
    }
}
