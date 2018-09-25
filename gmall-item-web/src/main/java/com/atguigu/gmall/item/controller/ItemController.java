package com.atguigu.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.SkuManageService;
import com.atguigu.gmall.service.SpuManageService;
import org.assertj.core.util.Compatibility;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuManageService skuManageService;
    @Reference
    SpuManageService spuManageService;

    @RequestMapping("{skuId}.html")
    public String index(@PathVariable String skuId,Model model){

      SkuInfo skuInfo =  skuManageService.getSkuInfoById(skuId);

        String spuId = skuInfo.getSpuId();


        List<SpuSaleAttr> spuSaleAttrs = spuManageService.getSpuSaleAttrListCheckBySku(spuId,skuId);

        model.addAttribute("skuInfo",skuInfo);

        model.addAttribute("spuSaleAttrListCheckBySku",spuSaleAttrs);

        List<SkuInfo> skuInfos = skuManageService.getSkuSaleAttrListCheckBySpu(spuId);

        HashMap<String, Object> hashMap = new HashMap<>();

        for (SkuInfo info : skuInfos) {
            String value = info.getId();
            String key = "";
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                String saleAttrValueId = skuSaleAttrValue.getSaleAttrValueId();
                key = key + "|" + saleAttrValueId;

            }
            hashMap.put(key,value);

        }
        String jsonString = JSON.toJSONString(hashMap);

        System.out.println(jsonString);
        model.addAttribute("json",jsonString);


        return "item";
    }


}
