package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParam;
import com.atguigu.gmall.constant.SearchConst;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<SkuLsInfo> getSkuLsInfoList(SkuLsParam skuLsParam) {

        Search build = new Search.Builder(getMyDsl(skuLsParam)).addIndex(SearchConst.SEARCH_INDEX).addType(SearchConst.SEARCH_TYPE).build();

        ArrayList<SkuLsInfo> skuLsInfos = new ArrayList<>();

        try {
            SearchResult execute = jestClient.execute(build);
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight != null){
                    List<String> skuName = highlight.get("skuName");
                    if (StringUtils.isNotBlank(skuName.get(0))){
                        source.setSkuName(skuName.get(0));
                    }
                }
                skuLsInfos.add(source);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


           return  skuLsInfos;
    }

    public String getMyDsl(SkuLsParam skuLsParam){


        String catalog3Id = skuLsParam.getCatalog3Id();

        String keyword = skuLsParam.getKeyword();

        String[] valueId = skuLsParam.getValueId();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (StringUtils.isNotBlank(catalog3Id)){

            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);

            boolQueryBuilder.filter(termQueryBuilder);
        }
        if (StringUtils.isNotBlank(keyword)){

            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);

            boolQueryBuilder.must(matchQueryBuilder);

        }
        if (null != valueId && valueId.length > 0){
            for (int i = 0; i <valueId.length ; i++) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId[i]);
                boolQueryBuilder.filter(termQueryBuilder);
            }

        }

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        System.err.println(searchSourceBuilder);
        return searchSourceBuilder.toString();
    }
}
