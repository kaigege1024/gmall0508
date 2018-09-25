package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.service.PaymentService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;


    /**
     * 保存paymentInfo表信息
     * @param paymentInfo
     */
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        paymentInfoMapper.insertSelective(paymentInfo);

    }

    /**
     * 更新paymentInfo表信息
     * @param paymentInfo
     */
    @Override
    public void updatePaymentInfoByOutTradeNo(PaymentInfo paymentInfo) {

        Example example = new Example(PaymentInfo.class);
        //用外部订单号来更新
        example.createCriteria().andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());

        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);


    }

    /**
     * 创建一个队列消息
     * @param outTradeNo
     * @param tradeNo
     */
    @Override
    public void sendPaymentResult(String outTradeNo, String tradeNo) {

        Connection connection = activeMQUtil.getConnection();

        try {
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_RESULT_QUEUE");
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo",outTradeNo);
            mapMessage.setString("tradeNo",tradeNo);
            MessageProducer producer = session.createProducer(queue);
            producer.send(mapMessage);
            session.commit();
            connection.close();
            producer.close();
            session.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
