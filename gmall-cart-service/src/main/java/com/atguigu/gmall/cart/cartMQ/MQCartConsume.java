package com.atguigu.gmall.cart.cartMQ;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class MQCartConsume {

    @Autowired
    CartService cartService;

    @JmsListener(destination = "CART_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void cartConsumeCheckResult(MapMessage mapMessage) throws JMSException {

        String userId = mapMessage.getString("userId");
        String cookieValue = mapMessage.getString("cookieValue");

        if (StringUtils.isNotBlank(userId)){

            cartService.combineCart(JSON.parseArray(cookieValue, CartInfo.class),userId);

        }

    }
}
