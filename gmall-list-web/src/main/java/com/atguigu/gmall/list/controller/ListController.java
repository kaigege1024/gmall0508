package com.atguigu.gmall.list.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    ListService listService;

    @Reference
    ManageService manageService;

    @RequestMapping("list.html")
    public String list(SkuLsParam skuLsParam, Model model){

    List<SkuLsInfo> skuLsInfous =  listService.getSkuLsInfoList(skuLsParam);

        HashSet<String> strings = new HashSet<>();
        for (SkuLsInfo skuLsInfo : skuLsInfous) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                strings.add(valueId);
            }
        }

        String join = StringUtils.join(strings, ",");

        List<BaseAttrInfo>  baseAttrInfos =  manageService.getAttrInfoByValueId(join);

        String[] valueId = skuLsParam.getValueId();

        List<Crumb> crumbList = new ArrayList<>();

        if (null != valueId&&valueId.length>0){
            for (String sid : valueId) {
                Crumb crumb = new Crumb();
                Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
                while (iterator.hasNext()){
                    BaseAttrInfo baseAttrInfo = iterator.next();

                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

                    for (BaseAttrValue baseAttrValue : attrValueList) {
                        String attrValueId = baseAttrValue.getId();
                        if (sid.equals(attrValueId)){

                            crumb.setValueName(baseAttrValue.getValueName());

                            iterator.remove();
                        }
                    }
                }
                String  crumbsUrlParam =    getUrlParam(skuLsParam,sid);
                crumb.setUrlParam(crumbsUrlParam);
                crumbList.add(crumb);

            }
        }



        String urlParam = getUrlParam(skuLsParam);

        model.addAttribute("attrList",baseAttrInfos);

        model.addAttribute("skuLsInfoList",skuLsInfous);

        model.addAttribute("urlParam",urlParam);

        model.addAttribute("attrValueSelectedList",crumbList);


        return "list";
    }

    private String getUrlParam(SkuLsParam skuLsParam,String...sid) {

        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();
        String urlParam = "";

        if (StringUtils.isNotBlank(catalog3Id)){
            if (StringUtils.isNotBlank(urlParam)){
                urlParam += "&";
            }
            urlParam += "catalog3Id="+catalog3Id;
        }
        if (StringUtils.isNotBlank(keyword)){

            if (StringUtils.isNotBlank(urlParam)){
                urlParam += "&";
            }
            urlParam += "keyword="+keyword;
        }
        if (sid != null&& sid.length > 0) {
            if (valueId != null && valueId.length > 0) {
                for (String id : valueId) {
                    for (int i = 0; i <sid.length ; i++) {
                        if (!id.equals(sid[i])) {
                            urlParam += "&valueId=" + id;
                        }
                    }

                }
            }
        }else{
             if (valueId != null && valueId.length > 0){
              for (String id : valueId) {
                urlParam += "&valueId="+id;
                   }

             }
        }
        return  urlParam;
    }

// private String getUrlParam(SkuLsParam skuLsParam) {
//
//        String catalog3Id = skuLsParam.getCatalog3Id();
//        String keyword = skuLsParam.getKeyword();
//        String[] valueId = skuLsParam.getValueId();
//        String urlParam = "";
//
//        if (StringUtils.isNotBlank(catalog3Id)){
//            if (StringUtils.isNotBlank(urlParam)){
//                urlParam += "&";
//            }
//            urlParam += "catalog3Id="+catalog3Id;
//        }
//        if (StringUtils.isNotBlank(keyword)){
//
//            if (StringUtils.isNotBlank(urlParam)){
//                urlParam += "&";
//            }
//            urlParam += "keyword="+keyword;
//        }
//        if (valueId != null && valueId.length > 0){
//            for (String id : valueId) {
//                urlParam += "&valueId="+id;
//                    }
//
//        }
//
//        return  urlParam;
//    }

}
