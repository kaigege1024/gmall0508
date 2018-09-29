package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.ActiveMQUtil;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
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

    @Autowired
    ActiveMQUtil activeMQUtil;

    /**
     * 客户提交订单，保存订单
     * @param orderInfo
     */
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

    /**
     * 随机生成一个交易码
     * @param userId
     * @return
     */
    @Override
    public String genTradeCode(String userId) {

        String uuid = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();

        jedis.setex(userId,1000*60*15,uuid);

        jedis.close();

        return uuid;
    }

    /**
     * 验证交易码是否可用，使用之后从缓存直接删除
     * @param userId
     * @param tradeCode
     * @return
     */
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

    /**
     * 支付成功，修改订单信息
     * @param orderInfo
     */
    @Override
    public void updateOrderInfo(OrderInfo orderInfo) {

        Example example = new Example(OrderInfo.class);

        example.createCriteria().andEqualTo("outTradeNo",orderInfo.getOutTradeNo());

        orderInfoMapper.updateByExampleSelective(orderInfo,example);


    }

    /**
     * 向MQ发送一个队列消息，通知库存系统
     * @param infoByTradeNo
     */
    @Override
    public void sendOrderResultQueue(OrderInfo infoByTradeNo) {

        Connection connection = activeMQUtil.getConnection();

        try {
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue resultQueue = session.createQueue("ORDER_RESULT_QUEUE");
            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(JSON.toJSONString(infoByTradeNo));
            MessageProducer producer = session.createProducer(resultQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
