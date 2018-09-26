package com.atguigu.gmall.payment.MQListenerPayment;


import com.atguigu.gmall.bean.enums.PaymentStatus;
import com.atguigu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

@Component
public class PaymentCheckQueueListener {


    @Autowired
    PaymentService paymentService;




    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumeCheckResult(MapMessage mapMessage) throws JMSException {

        String outTradeNo = mapMessage.getString("outTradeNo");
        Integer count = mapMessage.getInt("count");

        //调用支付宝的查询接口查询订单支付状态
        Map<String, String> map = paymentService.checkAlipayPayment(outTradeNo);

        String tradeStatus = map.get("tradeStatus");
        String tradeNo = map.get("tradeNo");
        String body = map.get("body");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
          //判断数据库里面的信息有没有被更改
          boolean b  =  paymentService.checkPaymentInfo(outTradeNo);
          if (!b){
              //支付成功返回以后修改paymentInfo表，给mq发送一个队列消息，通知订单服务
              paymentService.tradeSuccessUpdatePaymentInfo(outTradeNo,tradeNo,body);
          }else {
              System.err.println("检查到该比交易已经支付完毕，直接返回结果，消息队列任务结束");
          }



        } else {

            if (count > 0) {

                //订单没有支付，继续发送延迟队列消息
                System.err.println("对订单" + outTradeNo + "进行第" + (6 - count) + "次巡检");
                paymentService.sendDelayPaymentResult(outTradeNo, count - 1);

            } else {

                System.err.println("超过指定时间");
                //订单超过时间没有支付，此订单作废

            }
        }


    }


}
