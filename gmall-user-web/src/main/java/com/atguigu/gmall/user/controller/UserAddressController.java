package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAddressController {

    @Reference
    UserAddressService userAddressService;

    @RequestMapping(value = "/UserAddress")
    public ResponseEntity<List<UserAddress>>  getUserAddressAll(){

        List<UserAddress> userAddressAll = userAddressService.getUserAddressAll();

        return ResponseEntity.ok(userAddressAll);
    }

    @RequestMapping(value = "/addUserAddress")
    public ResponseEntity<Void> addUserAddress(UserAddress userAddress){

         userAddressService.addUserAddress(userAddress);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/updateUserAddress")
    public ResponseEntity<Void> updateUserAddress(UserAddress userAddress){

        userAddressService.updateUserAddress(userAddress);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/deleteUserAddress")
    public ResponseEntity<Void> deleteUserAddress(String id){

        userAddressService.deleteUserAddress(id);

        return ResponseEntity.ok().build();
    }

//    @RequestMapping(value = "/getUserAddress")
//    public ResponseEntity<UserAddress> getUserAddress(String id){
//
//        UserAddress userAddress = userAddressService.getUserAddress(id);
//
//        return ResponseEntity.ok(userAddress);
//    }

//    @RequestMapping(value = "/UserAddressList")
//    public ResponseEntity<UserAddress> getUserAddress(String userId){
//
//        userAddressService.UserAddressList(userId);
//
//        return ResponseEntity.ok();
//    }

}
