package com.atguigu.gmall.order.MQ;


import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
public class OrderPaymentResultListener {


    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    OrderService orderService;


    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {

        String outTradeNo = mapMessage.getString("outTradeNo");

        String tradeNo = mapMessage.getString("tradeNo");
        if (StringUtils.isNotBlank(tradeNo)){

            OrderInfo orderInfo = new OrderInfo();

            orderInfo.setOutTradeNo(outTradeNo);

            orderInfo.setOrderStatus("订单已支付");

            orderInfo.setProcessStatus("订单已支付");

            orderInfo.setTrackingNo(tradeNo);

            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.DATE,3);

            orderInfo.setExpectDeliveryTime(calendar.getTime());

            orderService.updateOrderInfo(orderInfo);

        }
    }
}
