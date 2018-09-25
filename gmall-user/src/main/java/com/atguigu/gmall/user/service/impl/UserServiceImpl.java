package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UserInfo> getAll() {

        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }


    @Override
    public void addUser(UserInfo user) {

        userInfoMapper.insertSelective(user);


    }

    @Override
    public void updateUser(UserInfo user) {

       userInfoMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void deleteUser(String id) {

        UserInfo userInfo = new UserInfo();

        userInfo.setId(id);

        userInfoMapper.delete(userInfo);
    }

    @Override
    public UserInfo getUser(String id) {

        UserInfo userInfo = new UserInfo();

        userInfo.setId(id);

        return userInfoMapper.selectOne(userInfo);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {


            UserInfo userInfoLogin = userInfoMapper.selectOne(userInfo);

            if (null != userInfoLogin){

                Jedis jedis = redisUtil.getJedis();

                jedis.setex(RedisConst.USER_PREFIX+userInfoLogin.getId()+RedisConst.USER_SUFFIX
                        ,RedisConst.USER_TIME_OUT, JSON.toJSONString(userInfoLogin));
            }

            return userInfoLogin;
        }


}
