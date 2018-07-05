package org.trc.form.impower;

import java.util.List;
import java.util.Set;

public class MenuNode {

    private Long parentCode;

    private Set<Long> codeList;

    private List<MenuNode> menuNodeList;

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

    public List<MenuNode> getMenuNodeList() {
        return menuNodeList;
    }

    public void setMenuNodeList(List<MenuNode> menuNodeList) {
        this.menuNodeList = menuNodeList;
    }
}
