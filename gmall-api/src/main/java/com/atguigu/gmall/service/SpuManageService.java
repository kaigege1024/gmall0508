package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface SpuManageService {

    List<SpuInfo> getSpuInfoList(String catalog3Id);

    List<SpuSaleAttr> getSpuSaleAttr(String spuId);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttrValue> getSpuSaleAttrValue(String spuId,String saleAttrId);

    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    List<SpuImage> spuImageList(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String spuId,String skuId);
}
