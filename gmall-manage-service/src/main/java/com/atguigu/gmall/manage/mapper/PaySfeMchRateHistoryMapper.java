package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.PaySfeMchRateHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface PaySfeMchRateHistoryMapper extends Mapper<PaySfeMchRateHistory> {



    void insertAll( List<PaySfeMchRateHistory> list);

}
