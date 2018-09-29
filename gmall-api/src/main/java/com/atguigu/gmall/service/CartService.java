package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;
import java.util.Map;

public interface CartService {
    
    
    CartInfo isCartInfoExist(CartInfo cartInfo);

    void updateCartInfo(CartInfo cartInfo);

    void addCartInfo(CartInfo cartInfo);

    void flushCartInfoCache(String userId);

    List<CartInfo> getCartCache(String userId);

    void updateCartInfoByUserId(CartInfo cartInfo);

    void combineCart(List<CartInfo> parseArray, String id);

    void deleteCarInfo(String join,String userId);

    void sendLoginSuccess(String cookieValue,String userId);
}
