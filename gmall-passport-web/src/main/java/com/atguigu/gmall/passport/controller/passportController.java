package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.applet.resources.MsgAppletViewer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class passportController {

    @Reference
    UserService userService;

    @Reference
    CartService cartService;


    @RequestMapping("index")
    public String  index(HttpServletRequest request, Model model){

        String originUrl = request.getParameter("originUrl");

        model.addAttribute("originUrl",originUrl);

        return "index";
    }

    /**
     * 验证用户名密码，生成token
     * @param userInfo
     * @param request
     * @return
     */

    @RequestMapping("login")
    @ResponseBody
    public String  login(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response){

    UserInfo userInfoLogin = userService.login(userInfo);
    String token = null;

    if (null != userInfoLogin){

        Map<String,String> map = new HashMap<>();

        String ip = getMyIpFormRequest(request);

        map.put("userId",userInfoLogin.getId());
        map.put("nickName",userInfoLogin.getNickName());

        token = JwtUtil.encode("atguigu0508", map, ip);

        //合并购物车
        String cookieValue = CookieUtil.getCookieValue(request, "listCartCookie", true);

        cartService.combineCart(JSON.parseArray(cookieValue,CartInfo.class),userInfoLogin.getId());

        cartService.flushCartInfoCache(userInfoLogin.getId());

        CookieUtil.deleteCookie(request,response,"listCartCookie");


    }else {

        request.setAttribute("msg", "用户名密码不匹配，请重新输入");
        return "err";
    }

        return token;
    }

    private String getMyIpFormRequest(HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        if (StringUtils.isBlank(ip)){

         ip = request.getHeader("x-forwarded-for");
        }
        return ip;
    }
    @RequestMapping("verify")
    @ResponseBody
    public String verifyToken(String token,String ip){

        Map atguigu0508 = JwtUtil.decode("atguigu0508",token,ip);

        if (null != atguigu0508){

            return "success";
        }else {

            return "fail";
        }

    }

}
