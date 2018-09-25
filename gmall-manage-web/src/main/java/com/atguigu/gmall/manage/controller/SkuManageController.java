package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.service.SkuManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuManageController {

    @Reference
    SkuManageService skuManageService;


    @RequestMapping("skuInfoListBySpu")
    @ResponseBody
    public List<SkuInfo> skuInfoListBySpu(String spuId){

        List<SkuInfo> skuInfos = skuManageService.getSkuInfoList(spuId);

        return skuInfos;

    }

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(SkuInfo skuInfo){

        skuManageService.saveSkuInfo(skuInfo);

        return "success";
    }
}
