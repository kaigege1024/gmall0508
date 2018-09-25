package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {


    List<UserInfo> getAll();


    void addUser(UserInfo user);


    void updateUser(UserInfo user);


     void deleteUser(String id);


     UserInfo getUser(String id);


    UserInfo login(UserInfo userInfo);
}
