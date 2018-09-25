package com.atguigu.gmall.user;

import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.bean.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallUserApplicationTests {
    @Autowired
    UserService userService;
    @Test
    public void contextLoads() {




    }

    @Test
    public void test1(){

        UserInfo userInfo = new UserInfo();
        userInfo.setId("1");
        userInfo.setName("2222");
        userService.updateUser(userInfo);
    }

}
