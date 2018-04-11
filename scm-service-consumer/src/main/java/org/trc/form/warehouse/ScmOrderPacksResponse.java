package org.trc.form.warehouse;

import java.util.List;

/**
 * Created by hzcyn on 2018/4/11.
 */
public class ScmOrderPacksResponse {

    /**
     * 物流信息集合
     */
    private List<ScmOrderDefaultResult> scmOrderDefaultResults;

    public List<ScmOrderDefaultResult> getScmOrderDefaultResults() {
        return scmOrderDefaultResults;
    }

    public void setScmOrderDefaultResults(List<ScmOrderDefaultResult> scmOrderDefaultResults) {
        this.scmOrderDefaultResults = scmOrderDefaultResults;
    }
}
