package com.atguigu.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("userList")
    public ResponseEntity<List<UserInfo>> getUserAll(){

        List<UserInfo> userInfoList = userService.getAll();

        return ResponseEntity.ok(userInfoList);
    }


    @RequestMapping(value = "/user",method = RequestMethod.POST)
    public ResponseEntity<Void>  addUser(UserInfo user){

        userService.addUser(user);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user",method = RequestMethod.PUT)
    public ResponseEntity<Void>  updateUser(UserInfo user){

        userService.updateUser(user);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user",method = RequestMethod.DELETE)
    public ResponseEntity<Void>  deleteUser(String id){

        userService.deleteUser(id);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/getUser")
    public ResponseEntity<UserInfo>  getUser(String id){

        UserInfo userInfo = userService.getUser(id);

        return ResponseEntity.ok(userInfo);
    }




}
