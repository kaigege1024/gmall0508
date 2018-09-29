package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.SpuManageService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpuManageServiceImpl implements SpuManageService {

    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    SpuImageMapper spuImageMapper;



    @Override
    public List<SpuInfo> getSpuInfoList(String catalog3Id) {

        SpuInfo spuInfo = new SpuInfo();

        spuInfo.setCatalog3Id(catalog3Id);

        List<SpuInfo> spuInfos = spuInfoMapper.select(spuInfo);

        return spuInfos;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(String spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();

        spuSaleAttr.setSpuId(spuId);

        List<SpuSaleAttr> select = spuSaleAttrMapper.select(spuSaleAttr);

        return select;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {

        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        String spuId = spuInfo.getId();

        if (spuId== null || "".equals(spuId)) {
            spuInfo.setId(null);

            spuInfoMapper.insertSelective(spuInfo);

             spuId = spuInfo.getId();

            List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();

            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuId);
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    spuSaleAttrValue.setSpuId(spuId);
                    spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                }
            }
            List<SpuImage> spuImageList = spuInfo.getSpuImageList();

            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuId);
                spuImageMapper.insertSelective(spuImage);
            }
        }else {

            spuInfoMapper.updateByPrimaryKeySelective(spuInfo);
            List<SpuSaleAttr> spuSaleAttrLists = spuInfo.getSpuSaleAttrList();
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrLists) {
                Example example = new Example(SpuSaleAttr.class);
                example.createCriteria().andEqualTo("spuId",spuId);
                spuSaleAttrMapper.deleteByExample(example);
                spuSaleAttr.setSpuId(spuId);
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueLists = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueLists) {
                    Example example1 = new Example(SpuSaleAttrValue.class);
                    example1.createCriteria().andEqualTo("spuId",spuId);
                    spuSaleAttrValueMapper.deleteByExample(example1);
                    spuSaleAttrValue.setSpuId(spuId);
                    spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                }

            }

        }
    }

    @Override
    public List<SpuSaleAttrValue> getSpuSaleAttrValue(String spuId,String saleAttrId) {
        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();

        spuSaleAttrValue.setSpuId(spuId);

        spuSaleAttrValue.setSaleAttrId(saleAttrId);

        List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.select(spuSaleAttrValue);
             return spuSaleAttrValues;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {

        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> select = spuSaleAttrMapper.select(spuSaleAttr);

        for (SpuSaleAttr saleAttr : select) {
            String saleAttrId = saleAttr.getSaleAttrId();

            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();

            spuSaleAttrValue.setSaleAttrId(saleAttrId);

            spuSaleAttrValue.setSpuId(spuId);

            List<SpuSaleAttrValue> select1 = spuSaleAttrValueMapper.select(spuSaleAttrValue);

            saleAttr.setSpuSaleAttrValueList(select1);


        }


                     return select;
    }

    @Override
    public List<SpuImage> spuImageList(String spuId) {

        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> select = spuImageMapper.select(spuImage);

        return select;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String spuId,String skuId) {

        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(Integer.parseInt(spuId),Integer.parseInt(skuId));

        return spuSaleAttrList;
    }
}
