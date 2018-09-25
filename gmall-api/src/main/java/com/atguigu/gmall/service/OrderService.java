package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {

    void saveOrderInfo(OrderInfo orderInfo);

    String genTradeCode(String userId);

    boolean checkTradeCode(String userId, String tradeCode);

    OrderInfo getOrderInfoByTradeNo(String outTradeNo);

    void updateOrderInfo(OrderInfo orderInfo);
}
