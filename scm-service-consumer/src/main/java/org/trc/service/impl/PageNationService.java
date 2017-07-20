package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.service.IPageNationService;
import org.trc.util.Pagenation;
import org.trc.util.TransportClientUtil;

import java.util.ArrayList;
import java.util.List;

@Service("pageNationService")
public class PageNationService implements IPageNationService {
    private Logger log = LoggerFactory.getLogger(PageNationService.class);

    public SearchHit[] resultES(SearchRequestBuilder srb, TransportClient clientUtil) throws Exception {
        MultiSearchResponse sr;
        SearchHit[] searchHists = new SearchHit[0];
        try {
            sr = clientUtil.prepareMultiSearch()
                    .add(srb)
                    .get();
        } catch (Exception e) {
            log.error("es搜索异常" + e.getMessage(), e);
            return searchHists;
        }
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            searchHists = response.getHits().getHits();

        }

        return searchHists;
    }

    @Override
    public SearchHit[] resultES2(SearchRequestBuilder srb, TransportClient clientUtil, Pagenation page, String name) throws Exception {
//        TransportClient clientUtil = TransportClientUtil.getTransportClient();
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<b style=\"color: red\">");
        hiBuilder.postTags("</b>");
        hiBuilder.highlighterType(name);
        srb.highlighter(hiBuilder).addSort(SortBuilders.fieldSort("update_time").order(SortOrder.DESC))
                .setFrom(page.getStart())//第几个开始
                .setSize(page.getPageSize());//长度
        MultiSearchResponse sr;
        SearchHit[] searchHists = new SearchHit[0];
        try {
            sr = clientUtil.prepareMultiSearch()
                    .add(srb)
                    .get();
        } catch (Exception e) {
            log.error("es搜索异常" + e.getMessage(), e);
            return searchHists;
        }
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            searchHists = response.getHits().getHits();
        }

        return searchHists;
    }

}
