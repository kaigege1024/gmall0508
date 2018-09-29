package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;


import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseCatalog1> getcatlog1() {


        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getcatlog2(String catalog1Id) {

        BaseCatalog2 baseCatalog2 = new BaseCatalog2();

        baseCatalog2.setCatalog1Id(catalog1Id);

        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getcatlog3(String catalog2Id) {

        BaseCatalog3 baseCatalog3 = new BaseCatalog3();

        baseCatalog3.setCatalog2Id(catalog2Id);

        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {

        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();

        baseAttrInfo.setCatalog3Id(catalog3Id);

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);

        for (BaseAttrInfo attrInfo : baseAttrInfos) {
            String attrId =  attrInfo.getId();
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrId);
            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
            attrInfo.setAttrValueList(baseAttrValues);

        }
           return  baseAttrInfos;

    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

       String id =  baseAttrInfo.getId();

       if (id == null || id.equals("")){
            baseAttrInfo.setId(null);

           baseAttrInfoMapper.insertSelective(baseAttrInfo);

           String attrId = baseAttrInfo.getId();

           List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

           for(BaseAttrValue baseAttrValue: attrValueList){

               baseAttrValue.setAttrId(attrId);

               baseAttrValueMapper.insertSelective(baseAttrValue);
          }

        }else{

           baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

           String attrId = baseAttrInfo.getId();

           Example example = new Example(BaseAttrValue.class);

            example.createCriteria().andEqualTo("attrId",attrId);

           baseAttrValueMapper.deleteByExample(example);

           List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

           for(BaseAttrValue baseAttrValue: attrValueList){

               baseAttrValue.setAttrId(attrId);

               baseAttrValueMapper.insertSelective(baseAttrValue);
           }
       }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        Example example = new Example(BaseAttrValue.class);

        example.createCriteria().andEqualTo("attrId",attrId);

        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectByExample(example);

            return baseAttrValues;
    }

    @Override
    public void deleteAttrInfo(String id) {

        Example example = new Example(BaseAttrValue.class);

        example.createCriteria().andEqualTo("attrId",id);

        baseAttrValueMapper.deleteByExample(example);

        Example example1 = new Example(BaseAttrInfo.class);

        example1.createCriteria().andEqualTo("id",id);

        baseAttrInfoMapper.deleteByExample(example1);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoByValueId(String join) {

        List<BaseAttrInfo> baseAttrInfos = null;

        if (StringUtils.isNotBlank(join)){

             baseAttrInfos =  baseAttrInfoMapper.selectAttrInfoByValueId(join);
        }


        return baseAttrInfos;
    }
}
