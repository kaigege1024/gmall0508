package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuManageService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Reference
    SkuManageService skuManageService;

    @Reference
    CartService cartService;


    @LoginRequire
    @RequestMapping("checkCart")
    public String checkCart(CartInfo cartInfo,HttpServletRequest request,HttpServletResponse response,Model model){

        List<CartInfo> cartInfos = new ArrayList<>();

        String userId = (String) request.getAttribute("userId");

        if (StringUtils.isNotBlank(userId)){

            cartInfo.setUserId(userId);

            cartService.updateCartInfoByUserId(cartInfo);

            cartService.flushCartInfoCache(userId);

            cartInfos = cartService.getCartCache(userId);



        }else {

            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);

            if (listCartCookie != null && listCartCookie.length()>0) {
                cartInfos = JSON.parseArray(listCartCookie, CartInfo.class);

                for (CartInfo info : cartInfos) {

                    if (cartInfo.getSkuId().equals(info.getSkuId())) {

                        info.setIsChecked(cartInfo.getIsChecked());
                    }
                }


                CookieUtil.setCookie(request, response, "listCartCookie", JSON.toJSONString(cartInfos), 1000 * 60 * 60 * 24, true);
            }


        }
        model.addAttribute("cartList",cartInfos);
        BigDecimal totalPrice = getCartTotalPice(cartInfos);
        model.addAttribute("totalPrice",totalPrice);

        return "cartListInner";


    }




    @LoginRequire
    @RequestMapping("cartList")
    public String cartList(Model model,HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");

        List<CartInfo> cartInfos = new ArrayList<CartInfo>();
        if (StringUtils.isBlank(userId)){

            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);

            cartInfos = JSON.parseArray(listCartCookie, CartInfo.class);

        }else {

            cartInfos =  cartService.getCartCache(userId);


        }
        BigDecimal totalPrice=null;
        if (null != cartInfos&& cartInfos.size()>0){
             totalPrice = getCartTotalPice(cartInfos);
        }


        model.addAttribute("cartList",cartInfos);
        model.addAttribute("totalPrice",totalPrice);

        return "cartList";
    }

    private BigDecimal getCartTotalPice(List<CartInfo> cartInfos) {

        BigDecimal totalPrice = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {

           String isChecked =  cartInfo.getIsChecked();

            if ("1".equals(isChecked)){

                totalPrice = totalPrice.add(cartInfo.getCartPrice());


            }

        }

        return  totalPrice;

    }

    @LoginRequire
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> map,Model model) {
        String skuId = map.get("skuId");
        String skuNum = map.get("num");
        CartInfo cartInfo = new CartInfo();
        SkuInfo skuInfo = skuManageService.getSkuInfoById(skuId);
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuNum(Integer.parseInt(skuNum));
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(skuNum)));
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setIsChecked("1");
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        if (StringUtils.isBlank(userId)) {
            cartInfo.setUserId(userId);
            String cookieValue = CookieUtil.getCookieValue(request, "listCartCookie", true);
            if (StringUtils.isBlank(cookieValue)) {
                cartInfos.add(cartInfo);
            } else {
                cartInfos = JSON.parseArray(cookieValue, CartInfo.class);
                boolean b = ifNewCart(cartInfos, cartInfo);
                if (b) {
                    cartInfos.add(cartInfo);
                } else {
                    for (CartInfo info : cartInfos) {
                        if (info.getSkuId().equals(skuId)) {
                            info.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }

                    }
                }
            }
            CookieUtil.setCookie(request, response, "listCartCookie", JSON.toJSONString(cartInfos), 1000 * 60 * 60 * 24, true);

        }else {

                cartInfo.setUserId(userId);

             CartInfo cartInfoExist  = cartService.isCartInfoExist(cartInfo);

             if (null != cartInfoExist){
                 cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + cartInfo.getSkuNum());
                 cartInfoExist.setCartPrice(cartInfoExist.getSkuPrice().multiply(new BigDecimal(cartInfoExist.getSkuNum())));
                 cartService.updateCartInfo(cartInfoExist);
             }else{

                 cartService.addCartInfo(cartInfo);
             }

             cartService.flushCartInfoCache(userId);

        }

        model.addAttribute("skuInfo",skuInfo);
        model.addAttribute("skuNum",cartInfo.getSkuNum());

        return "redirect:catSuccess";
    }

    private boolean ifNewCart(List<CartInfo> cartInfos, CartInfo cartInfo) {
        boolean b = true;
        for (CartInfo info : cartInfos) {
            if (info.getSkuId().equals(cartInfo.getSkuId())) {
                b = false;
            }
        }
        return b;

    }

    @RequestMapping("catSuccess")
    public String catSuccess() {


        return "success";
    }
}
