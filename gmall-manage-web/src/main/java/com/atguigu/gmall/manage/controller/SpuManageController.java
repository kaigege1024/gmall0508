package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.util.FileUploadUtil;
import com.atguigu.gmall.service.SpuManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class SpuManageController {

    @Reference
    SpuManageService spuManageService;

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){

        List<SpuImage> spuImages = spuManageService.spuImageList(spuId);

        return spuImages;
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){

        List<SpuSaleAttr> spuSaleAttrs = spuManageService.getSpuSaleAttrList(spuId);

        return spuSaleAttrs;
    }





    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public  String saveSpuInfo(SpuInfo spuInfo){

        spuManageService.saveSpuInfo(spuInfo);

        return "success";
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){


        String imgUrl = FileUploadUtil.fileUploadImg(file);

        return imgUrl;
    }




    @RequestMapping("getSpuInfoList")
    @ResponseBody
    public List<SpuInfo> getSpuInfoList(@RequestParam Map<String,String> map){
       String catalog3Id =  map.get("catalog3Id");

        List<SpuInfo> spuInfos = spuManageService.getSpuInfoList(catalog3Id);

        return  spuInfos;

    }

    @RequestMapping("getSpuSaleAttr")
    @ResponseBody
    public List<SpuSaleAttr> getSpuSaleAttr(@RequestParam Map<String,String> map){
        String spuId =  map.get("spuId");

        List<SpuSaleAttr> spuSaleAttrs = spuManageService.getSpuSaleAttr(spuId);

        return  spuSaleAttrs;

    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){


        List<BaseSaleAttr> baseSaleAttrs = spuManageService.baseSaleAttrList();

        return  baseSaleAttrs;

    }

    @RequestMapping("spuSaleAttrValue")
    @ResponseBody
    public List<SpuSaleAttrValue> spuSaleAttrValue(@RequestParam Map<String,String> map){
            String spuId = map.get("spuId");
            String saleAttrId = map.get("saleAttrId");

        List<SpuSaleAttrValue> spuSaleAttrValues = spuManageService.getSpuSaleAttrValue(spuId,saleAttrId);

        return  spuSaleAttrValues;

    }

}
