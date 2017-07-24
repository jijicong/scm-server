package org.trc.model;

import org.elasticsearch.search.SearchHit;

public class SearchResult {
    private SearchHit[] searchHits;
    private int count;

    public SearchHit[] getSearchHits() {
        return searchHits;
    }

    public void setSearchHits(SearchHit[] searchHits) {
        this.searchHits = searchHits;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
