package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;

import java.util.List;

public interface UserAddressService {


    List<UserAddress> getUserAddressAll();


    void addUserAddress(UserAddress userAddress);


    void updateUserAddress(UserAddress userAddress);


    void deleteUserAddress(String id);

    List<UserAddress> getUserAddressByUserId(String userId);


    UserAddress getUserAddressById(String addressId);
}
