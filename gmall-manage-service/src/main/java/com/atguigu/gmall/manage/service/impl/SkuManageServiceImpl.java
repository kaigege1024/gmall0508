package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuManageService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;

@Service
public class SkuManageServiceImpl implements SkuManageService {

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<SkuInfo> getSkuInfoList(String spuId) {

        SkuInfo skuInfo = new SkuInfo();

        skuInfo.setSpuId(spuId);

        List<SkuInfo> select = skuInfoMapper.select(skuInfo);

        return select;
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        skuInfoMapper.insertSelective(skuInfo);

        String skuId = skuInfo.getId();

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();

        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);

            skuAttrValueMapper.insertSelective(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);

            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }


    }
    @Override
    public SkuInfo getSkuInfoById(String skuId) {
        SkuInfo skuInfo = null;

        try {
            Jedis jedis = redisUtil.getJedis();
            String skuKey = RedisConst.SKU_PREFIX+skuId +RedisConst.SKU_SUFFIX;
            String skunInfoJson = jedis.get(skuKey);

            if (skunInfoJson == null || skunInfoJson.length() == 0){
             System.err.println(Thread.currentThread().getName()+"未命中缓存");
             String skuLockKey = RedisConst.SKU_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
             String lock = jedis.set(skuLockKey, "OK", "NX", "PX", RedisConst.SKULOCK_EXPIRE_PX);

                if ("OK".equals(lock)){
                    System.err.println(Thread.currentThread().getName()+"获得分布式锁");
                     skuInfo = getSkuInfoDB(skuId);
                    System.err.println(Thread.currentThread().getName()+"删除分布式锁");
                     jedis.del(RedisConst.SKU_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX);

                     if (skuInfo == null){
                         System.err.println(Thread.currentThread().getName()+"从数据库获得空值，同步redis");
                         jedis.set(skuKey,"empty");
                         return  null;
                     }
                    String skuInfoJsonNew = JSON.toJSONString(skuInfo);
                     jedis.set(skuKey,skuInfoJsonNew);
                     jedis.close();
                     return  skuInfo;

                }else {
                    System.err.println(Thread.currentThread().getName()+"未获得分布式锁，开始自旋");
                    Thread.sleep(1000);
                    jedis.close();
                    return  getSkuInfoById1(skuId);
                }
            }else if (skunInfoJson.equals("empty")){
                return null;
            }else {
                System.err.println(Thread.currentThread().getName()+"命中缓存");
                skuInfo = JSON.parseObject(skunInfoJson,SkuInfo.class);
                jedis.close();
                return skuInfo;
            }
        }catch (JedisConnectionException e){
                e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return skuInfo;

    }




    public SkuInfo getSkuInfoById1(String skuId) {

        Jedis jedis = redisUtil.getJedis();
        String skuKey = RedisConst.SKU_PREFIX+skuId +RedisConst.SKU_SUFFIX;
        String skuInfoJson = jedis.get(skuKey);

        if (skuInfoJson != null){
            System.err.println(Thread.currentThread().getName()+"命中缓存");
            SkuInfo skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
            jedis.close();
            return skuInfo;
        }else {
            System.err.println(Thread.currentThread().getName()+"未命中缓存");

            System.err.println(Thread.currentThread().getName()+"查询数据");

            SkuInfo skuInfoDB = getSkuInfoDB(skuId);
            String skuInfoJsonStr = JSON.toJSONString(skuInfoDB);
            System.err.println(Thread.currentThread().getName()+"保存数据到redis");
            jedis.set(skuKey,skuInfoJsonStr);

            jedis.close();

            return skuInfoDB;
        }
    }

 public SkuInfo getSkuInfoDB(String skuId) {

        SkuInfo skuInfoParam = new SkuInfo();

        skuInfoParam.setId(skuId);

        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoParam);

        SkuImage skuImageParam = new SkuImage();

        skuImageParam.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImageParam);

        skuInfo.setSkuImageList(skuImages);


        return skuInfo;
    }


    @Override
    public List<SkuInfo> getSkuSaleAttrListCheckBySpu(String spuId) {

        List<SkuInfo> skuInfos = skuInfoMapper.selectSkuSaleAttrListCheckBySpu(Integer.parseInt(spuId));

        return skuInfos;
    }

    @Override
    public List<SkuInfo> getSkuInfoByCatalog3Id(String catalog3Id) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);
        for (SkuInfo info : skuInfos) {

            String skuId = info.getId();
            SkuImage skuImage = new SkuImage();
            skuImage.setSkuId(skuId);
            List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
            info.setSkuImageList(skuImageList);

            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(skuId);
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);

                info.setSkuAttrValueList(skuAttrValues);

            }

             return skuInfos;
    }


}
