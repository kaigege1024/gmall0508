package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveOrderInfo(OrderInfo orderInfo) {

        orderInfoMapper.insertSelective(orderInfo);

        String orderId = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        for (OrderDetail orderDetail : orderDetailList) {

            orderDetail.setOrderId(orderId);

            orderDetailMapper.insertSelective(orderDetail);
        }


    }

    @Override
    public String genTradeCode(String userId) {

        String uuid = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();

        jedis.setex(userId,1000*60*15,uuid);

        jedis.close();

        return uuid;
    }

    @Override
    public boolean checkTradeCode(String userId, String tradeCode) {

        boolean b = false;

        if (StringUtils.isNotBlank(tradeCode)){

            Jedis jedis = redisUtil.getJedis();

            String s = jedis.get(userId);

            if (tradeCode.equals(s)){

                b = true;

                jedis.del(userId);
            }
            jedis.close();
        }

        return  b;
    }

    /**
     * 通过外部订单号，查询OrderInfo
     * @param outTradeNo
     * @return
     */
    @Override
    public OrderInfo getOrderInfoByTradeNo(String outTradeNo) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setOutTradeNo(outTradeNo);

        orderInfo = orderInfoMapper.selectOne(orderInfo);

        String orderId = orderInfo.getId();

        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setOrderId(orderId);

        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetails);

        return orderInfo;
    }

    @Override
    public void updateOrderInfo(OrderInfo orderInfo) {

        Example example = new Example(OrderInfo.class);

        example.createCriteria().andEqualTo("outTradeNo",orderInfo.getOutTradeNo());

        orderInfoMapper.updateByExampleSelective(orderInfo,example);


    }
}
