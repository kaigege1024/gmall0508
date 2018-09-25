package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class AttrManageControlle {


    @Reference
    ManageService manageService;

    @RequestMapping("/getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){

        List<BaseCatalog1> baseCatalog1s =  manageService.getcatlog1();

        return baseCatalog1s;
    }

    @RequestMapping("/getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(@RequestParam Map<String,String> map){
        String catalog1Id = map.get("catalog1Id");

        List<BaseCatalog2> baseCatalog2s =  manageService.getcatlog2(catalog1Id);

        return baseCatalog2s;
    }

    @RequestMapping("/getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(@RequestParam Map<String,String> map){
        String catalog2Id = map.get("catalog2Id");

        List<BaseCatalog3> baseCatalog3s =  manageService.getcatlog3(catalog2Id);

        return baseCatalog3s;
    }

    @RequestMapping("/getAttrList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrList(@RequestParam Map<String,String> map){
        String catalog3Id = map.get("catalog3Id");

        List<BaseAttrInfo> baseAttrInfos =  manageService.getAttrList(catalog3Id);

        return baseAttrInfos;
    }
    @RequestMapping("/saveAttr")
    @ResponseBody
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo){

        manageService.saveAttrInfo(baseAttrInfo);

        return "success";

    }
    @RequestMapping("attrValueList")
    @ResponseBody
    public List<BaseAttrValue> attrValueList(String attrId){

        List<BaseAttrValue> baseAttrValues = manageService.getAttrValueList(attrId);

        return baseAttrValues;
    }

    @RequestMapping("deleteAttrInfo")
    @ResponseBody
    public String deleteAttrInfo(String id){

        manageService.deleteAttrInfo(id);

        return "success";
    }


}
