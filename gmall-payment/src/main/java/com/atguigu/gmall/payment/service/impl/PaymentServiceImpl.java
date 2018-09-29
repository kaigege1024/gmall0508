package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.service.PaymentService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    AlipayClient alipayClient;


    /**
     * 保存paymentInfo表信息
     *
     * @param paymentInfo
     */
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        paymentInfoMapper.insertSelective(paymentInfo);

    }

    /**
     * 更新paymentInfo表信息
     *
     * @param paymentInfo
     */
    @Override
    public void updatePaymentInfoByOutTradeNo(PaymentInfo paymentInfo) {

        Example example = new Example(PaymentInfo.class);
        //用外部订单号来更新
        example.createCriteria().andEqualTo("outTradeNo", paymentInfo.getOutTradeNo());

        paymentInfoMapper.updateByExampleSelective(paymentInfo, example);


    }

    /**
     * 创建一个队列消息
     *
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
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setString("tradeNo", tradeNo);
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

    /**
     * 发送一个监听支付服务的延迟队列消息
     *
     * @param outTradeNo
     * @param i
     */
    @Override
    public void sendDelayPaymentResult(String outTradeNo, int i) {

        System.err.println("发送延迟队列消息");
        Connection connection = activeMQUtil.getConnection();

        try {
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue checkQueue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setInt("count", i);

            MessageProducer producer = session.createProducer(checkQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000*100);
            producer.send(mapMessage);
            session.commit();

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    /**
     * 调用支付宝查询接口，查询支付状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public Map<String,String> checkAlipayPayment(String outTradeNo) {

        Map<String,String> returnMap = new HashMap<String,String>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        Map<String,String> map = new HashMap<>();

        map.put("out_trade_no",outTradeNo);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
            String tradeStatus = response.getTradeStatus();
            if (StringUtils.isNotBlank(tradeStatus)){

                returnMap.put("tradeStatus",tradeStatus);
                returnMap.put("tradeNo", response.getTradeNo());
                returnMap.put("body",response.getBody());
                return returnMap;
            }else {
                returnMap.put("fail","fail");
                return returnMap;
            }
        } else {
            System.out.println("调用失败");
            returnMap.put("fail","fail");
            return returnMap;
        }

    }

    @Override
    public void tradeSuccessUpdatePaymentInfo(String outTradeNo, String tradeNo, String callBack) {

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(outTradeNo);

        paymentInfo.setAlipayTradeNo(tradeNo);

        paymentInfo.setCallbackTime(new Date());

        paymentInfo.setPaymentStatus("已支付");

        paymentInfo.setCallbackContent(callBack);
        //更新paymentInfo表
        updatePaymentInfoByOutTradeNo(paymentInfo);
        //创建一个队列消息
        sendPaymentResult(outTradeNo,tradeNo);

    }

    @Override
    public boolean checkPaymentInfo(String outTradeNo) {

        boolean b = false;
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(outTradeNo);

        paymentInfo = paymentInfoMapper.selectOne(paymentInfo);
        if (paymentInfo.getPaymentStatus().equals("已支付")){

            b = true;
        }

        return b;
    }
}
