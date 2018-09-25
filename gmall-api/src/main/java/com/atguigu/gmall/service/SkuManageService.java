package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface SkuManageService {

    List<SkuInfo> getSkuInfoList(String spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    SkuInfo getSkuInfoById(String skuId);

    List<SkuInfo> getSkuSaleAttrListCheckBySpu(String spuId);

    List<SkuInfo> getSkuInfoByCatalog3Id(String catalog3Id);
}
