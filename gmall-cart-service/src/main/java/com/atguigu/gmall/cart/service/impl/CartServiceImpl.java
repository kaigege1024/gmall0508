package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.constant.CartCacheConst;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.ActiveMQUtil;
import com.atguigu.gmall.util.RedisUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ActiveMQUtil activeMQUtil;


    @Override
    public CartInfo isCartInfoExist(CartInfo cartInfo) {

        Example example = new Example(CartInfo.class);

        example.createCriteria().andEqualTo("userId",cartInfo.getUserId()).andEqualTo("skuId",cartInfo.getSkuId());

        CartInfo cartInfoReturn = cartInfoMapper.selectOneByExample(example);

        return cartInfoReturn;
    }

    @Override
    public void updateCartInfo(CartInfo cartInfo) {

        cartInfoMapper.updateByPrimaryKeySelective(cartInfo);

    }

    @Override
    public void addCartInfo(CartInfo cartInfo) {

        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void flushCartInfoCache(String userId) {

        Jedis jedis = redisUtil.getJedis();

        CartInfo cartInfo = new CartInfo();

        cartInfo.setUserId(userId);

        List<CartInfo> cartInfoList = cartInfoMapper.select(cartInfo);

        if (null != cartInfoList&&cartInfoList.size()>0){
            HashMap<String, String> map = new HashMap<>();

            for (CartInfo info : cartInfoList) {
                String toJSONString = JSON.toJSONString(info);
                map.put(info.getId(),toJSONString);
            }
            jedis.del(CartCacheConst.CART_CACHE_PREFIX + userId + CartCacheConst.CART_CACHE_SUFFIX);
            jedis.hmset(CartCacheConst.CART_CACHE_PREFIX + userId + CartCacheConst.CART_CACHE_SUFFIX, map);

        }else {
            jedis.del(CartCacheConst.CART_CACHE_PREFIX + userId + CartCacheConst.CART_CACHE_SUFFIX);
        }

    }

    @Override
    public List<CartInfo> getCartCache(String userId) {

        List<CartInfo> cartInfos = new ArrayList<>();

        Jedis jedis = redisUtil.getJedis();

        List<String> hvals = jedis.hvals(CartCacheConst.CART_CACHE_PREFIX + userId + CartCacheConst.CART_CACHE_SUFFIX);

        if (null != hvals&& hvals.size()>0){
            for (String hval : hvals) {

                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);

                cartInfos.add(cartInfo);
            }
        }else {

            CartInfo cartInfo = new CartInfo();

            cartInfo.setUserId(userId);

            cartInfos = cartInfoMapper.select(cartInfo);

        }


        return cartInfos;
    }


    @Override
    public void updateCartInfoByUserId(CartInfo cartInfo) {

        Example example = new Example(CartInfo.class);

        example.createCriteria().andEqualTo("userId",cartInfo.getUserId())

                .andEqualTo("skuId",cartInfo.getSkuId());

        cartInfoMapper.updateByExampleSelective(cartInfo,example);

    }

    @Override
    public void combineCart(List<CartInfo> cartInfos, String userId) {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfoDBList = cartInfoMapper.select(cartInfo);
        if (null != cartInfos&&cartInfos.size()>0) {
            boolean b = true;
            for (CartInfo cartInfoCookie : cartInfos) {
                if (cartInfoDBList != null && cartInfoDBList.size() > 0) {
                    b = if_new_cart(cartInfoDBList, cartInfoCookie);
                }
                if (!b) {

                    CartInfo cartInfoDB = new CartInfo();
                    for (CartInfo Info : cartInfoDBList) {
                        if (Info.getSkuId().equals(cartInfoCookie.getSkuId())) {
                            cartInfoDB = Info;
                        }
                    }
                    cartInfoDB.setIsChecked(cartInfoCookie.getIsChecked());
                    cartInfoDB.setSkuNum(cartInfoCookie.getSkuNum());
                    cartInfoDB.setCartPrice(cartInfoDB.getSkuPrice().multiply(new BigDecimal(cartInfoDB.getSkuNum())));
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                }else {
                    cartInfoCookie.setUserId(userId);
                    cartInfoMapper.insertSelective(cartInfoCookie);

                }

            }

        }
        flushCartInfoCache(userId);
    }

    @Override
    public void deleteCarInfo(String join,String userId) {

        cartInfoMapper.deleteCarInfoById(join);

        flushCartInfoCache(userId);


    }

    @Override
    public void sendLoginSuccess(String cookieValue,String userId) {

        Connection connection = null;

        try {
            connection = activeMQUtil.getConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            MapMessage mapMessage = new ActiveMQMapMessage();

            mapMessage.setString("userId",userId);

            mapMessage.setString("cookieValue",cookieValue);

            Queue queue = session.createQueue("CART_CHECK_QUEUE");

            MessageProducer producer = session.createProducer(queue);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            producer.send(mapMessage);

            session.commit();

            producer.close();

            session.close();

            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }

    private boolean if_new_cart(List<CartInfo> listCartDb, CartInfo cartInfo) {

        boolean b = true;

        for (CartInfo info : listCartDb) {
            if (info.getSkuId().equals(cartInfo.getSkuId())) {
                b = false;
            }
        }

        return b;
    }

}
