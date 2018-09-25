package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class UserAddressServcieImpl implements UserAddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getUserAddressAll() {

        return userAddressMapper.selectAll();
    }

    @Override
    public void addUserAddress(UserAddress userAddress) {

        userAddressMapper.insertSelective(userAddress);
    }

    @Override
    public void updateUserAddress(UserAddress userAddress) {
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Override
    public void deleteUserAddress(String id) {

        UserAddress userAddress = new UserAddress();

        userAddress.setId(id);

        userAddressMapper.delete(userAddress);
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {


           UserAddress userAddress = new UserAddress();

           userAddress.setUserId(userId);

           List<UserAddress> userAddresses = userAddressMapper.select(userAddress);

           return userAddresses;

    }

    @Override
    public UserAddress getUserAddressById(String addressId) {

        UserAddress userAddress = new UserAddress();

        userAddress.setId(addressId);

        UserAddress selectOne = userAddressMapper.selectOne(userAddress);

        return selectOne;
    }


}
