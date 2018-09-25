package com.atguigu.gmall.list;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParam;
import com.atguigu.gmall.service.SkuManageService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Reference
    SkuManageService skuManageService;

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() {

        ArrayList<SkuLsInfo> skuLsInfos = new ArrayList<>();

        Search build = new Search.Builder(getMyDsl()).addIndex("gmall0508").addType("SkuInfo").build();

        try {
            SearchResult execute = jestClient.execute(build);

            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);

            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {

                SkuLsInfo source = hit.source;

                skuLsInfos.add(source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println(skuLsInfos.size());

    }

    public String getMyDsl(){

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id","61");

        boolQueryBuilder.filter(termQueryBuilder);

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "小米");

        boolQueryBuilder.must(matchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.from(0);

        searchSourceBuilder.size(100);

       return  searchSourceBuilder.toString();
    }


    @Test
    public void add() {

        List<SkuInfo> skuInfos =  skuManageService.getSkuInfoByCatalog3Id("61");

        List<SkuLsInfo> skuLsInfos = new ArrayList<>();

        for (SkuInfo skuInfo : skuInfos) {

            SkuLsInfo skuLsInfo = new SkuLsInfo();

            BeanUtils.copyProperties(skuInfo,skuLsInfo);

            skuLsInfos.add(skuLsInfo);
        }

        for (SkuLsInfo skuLsInfo : skuLsInfos) {

            Index build = new Index.Builder(skuLsInfo).index("gmall0508").type("SkuInfo").id(skuLsInfo.getId()).build();

            try {
                jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}
