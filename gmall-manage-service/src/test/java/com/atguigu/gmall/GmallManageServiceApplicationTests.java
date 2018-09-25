package com.atguigu.gmall;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;
import com.atguigu.gmall.manage.mapper.BaseCatalog2Mapper;
import com.atguigu.gmall.manage.mapper.BaseCatalog3Mapper;
import com.atguigu.gmall.util.RedisUtil;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;




    @Test
    public void contextLoads() {

        Jedis jedis = redisUtil.getJedis();
        String ping = jedis.ping();
        System.out.println(ping);

    }


    @Test
    public void getCatalogId() throws IOException {

        String catalog1Id = "11";

        BaseCatalog2 baseCatalog2 = new BaseCatalog2();

        baseCatalog2.setCatalog1Id(catalog1Id);

        List<BaseCatalog2> baseCatalog2List = baseCatalog2Mapper.select(baseCatalog2);

        for (BaseCatalog2 catalog2 : baseCatalog2List) {

            String catalog2Id = catalog2.getId();

            BaseCatalog3 baseCatalog3 = new BaseCatalog3();

            baseCatalog3.setCatalog2Id(catalog2Id);

            List<BaseCatalog3> baseCatalog3List = baseCatalog3Mapper.select(baseCatalog3);

            catalog2.setBaseCatalog3List(baseCatalog3List);


        }

        Object toJSON = JSON.toJSON(baseCatalog2List);


        FileOutputStream fileOutputStream = new FileOutputStream("catalog1Id.json");

        String jsonString = JSON.toJSONString(toJSON);

//        JSON.writeJSONString(fileOutputStream, toJSON, SerializerFeature.QuoteFieldNames);

//        System.err.println(toJSON);
//        byte[] bytes = jsonString.getBytes();
//
//       int l = jsonString.length();
//
//        try {
//            bufferedOutputStream.write(bytes,0,l);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                bufferedOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }

}
