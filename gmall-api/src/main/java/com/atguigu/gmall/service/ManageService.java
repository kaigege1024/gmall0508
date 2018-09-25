package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {


    List<BaseCatalog1> getcatlog1();


    List<BaseCatalog2> getcatlog2(String catalog1Id);

    List<BaseCatalog3> getcatlog3(String catalog2Id);

    List<BaseAttrInfo> getAttrList(String catalog3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void deleteAttrInfo(String id);

    List<BaseAttrInfo> getAttrInfoByValueId(String join);
}
