package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@Param("spuId") Integer spuId,@Param("skuId") Integer skuId);
}
