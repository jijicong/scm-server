package org.trc.service;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.trc.util.Pagenation;

public interface IPageNationService {
    SearchHit[] resultES(SearchRequestBuilder srb, TransportClient clientUtil) throws Exception;

    SearchHit[] resultES2(SearchRequestBuilder srb, TransportClient clientUtil ,Pagenation page,String name) throws Exception;

}
