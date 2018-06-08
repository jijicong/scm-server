package org.trc.form.impower;

import java.util.Set;

public class MenuNode {

    private Long parentCode;

    private Set<Long> codeList;

    private MenuNode menuNode;

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }

    public Set<Long> getCodeList() {
        return codeList;
    }

    public void setCodeList(Set<Long> codeList) {
        this.codeList = codeList;
    }

    public MenuNode getMenuNode() {
        return menuNode;
    }

    public void setMenuNode(MenuNode menuNode) {
        this.menuNode = menuNode;
    }
}
