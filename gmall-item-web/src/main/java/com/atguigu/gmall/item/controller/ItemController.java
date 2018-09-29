package com.atguigu.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.SkuManageService;
import com.atguigu.gmall.service.SpuManageService;
import com.atguigu.gmall.util.GetIpUtil;
import com.atguigu.gmall.util.HttpClientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Compatibility;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuManageService skuManageService;
    @Reference
    SpuManageService spuManageService;

    @RequestMapping("{skuId}.html")
    public String index(@PathVariable String skuId, Model model, HttpServletRequest request){

        String token = request.getParameter("token");
        if (StringUtils.isNotBlank(token)){
            String ip = GetIpUtil.getMyIpFormRequest(request);
            Map map = JwtUtil.decode("atguigu0508", token, ip);

            String nickName = (String) map.get("nickName");
            model.addAttribute("nickName",nickName);
        }

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

        String url = "http://www.gware.com:9001/hasStock?skuId="+skuId+"&num="+1;
        String s1 = HttpClientUtil.doGet(url);

        model.addAttribute("stock",s1);
        System.out.println(jsonString);
        model.addAttribute("json",jsonString);
        model.addAttribute("skuId",skuId);


        return "item";
    }

}
