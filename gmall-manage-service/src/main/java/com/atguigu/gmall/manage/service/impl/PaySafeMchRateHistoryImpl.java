package com.atguigu.gmall.manage.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PaySfeMchRateHistory;
import com.atguigu.gmall.manage.mapper.PaySfeMchRateHistoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


import javax.persistence.Id;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaySafeMchRateHistoryImpl {


    private static final int THRESHOLD = 1000;

    @Autowired
    PaySfeMchRateHistoryMapper paySfeMchRateHistoryMapper;


    public void insertSql(String filePath){

        File file = new File(filePath);
        //排除不可读的状态
        if (!file.exists() || file.isDirectory() || !file.canRead()){

            return;
        }
        BufferedReader reader = null;
        //读取文件
        try {
            reader  = new BufferedReader(new FileReader(file));
            //存储对象集合
            List<PaySfeMchRateHistory> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null){
                //将读取的数据转换成对象
                PaySfeMchRateHistory paySfeMchRateHistory = buildPaySfeMchRateHistory(line);
                if (paySfeMchRateHistory == null){
                    continue;
                }

                list.add(paySfeMchRateHistory);

                //分批插入
                if (list.size() == THRESHOLD){

                    paySfeMchRateHistoryMapper.insertAll(list);

                    list.clear();
                }

            }
            if (list.size()>0){

                paySfeMchRateHistoryMapper.insertAll(list);
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PaySfeMchRateHistory  buildPaySfeMchRateHistory(String line) {
        //查看是否有数据
        if (StringUtils.isBlank(line)){

         return null;
        }
        //拆分成数组
        String[] fields = line.split(",");
        //过滤数据
        if ("00030006".equals(fields[3])){
            return null;
        }

        String mermchId = fields[0];
        if (StringUtils.isNotBlank(mermchId)){


        }


        PaySfeMchRateHistory paySfeMchRateHistory = new PaySfeMchRateHistory();




        if ("0000007".equals(fields[2])){
            paySfeMchRateHistory.setPayCenterId(44);
        }else if ("0000008".equals(fields[2])){
            paySfeMchRateHistory.setPayCenterId(45);
        }else {
            return null;
        }
        paySfeMchRateHistory.setMerchantId(fields[0]);
        paySfeMchRateHistory.setProductCode(fields[1]);
        paySfeMchRateHistory.setServiceCode(fields[2]);
        paySfeMchRateHistory.setSubServiceCode(fields[3]);
        paySfeMchRateHistory.setRateType(fields[4]);
        paySfeMchRateHistory.setRatePercent((int)Double.parseDouble(fields[5])*10000);
        paySfeMchRateHistory.setRateMaxValue((int)Double.parseDouble(fields[6])*10000);
        paySfeMchRateHistory.setRateMinValue((int)Double.parseDouble(fields[7])*10000);
        paySfeMchRateHistory.setRateFixedValue((int)Double.parseDouble(fields[8])*10000);



        return paySfeMchRateHistory;
    }

}