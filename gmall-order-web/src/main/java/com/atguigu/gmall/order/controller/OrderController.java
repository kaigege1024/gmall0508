package com.atguigu.gmall.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.bean.enums.OrderStatus;
import com.atguigu.gmall.bean.enums.PaymentWay;
import com.atguigu.gmall.bean.enums.ProcessStatus;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.SkuManageService;
import com.atguigu.gmall.service.UserAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserAddressService userAddressService;

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;

    @Reference
    SkuManageService skuManageService;

    /**
     * 提交订单的方法
     * @param request
     * @param addressId
     * @return
     */

    @LoginRequire(autoRedirect = false)
    @RequestMapping("submitOrder")
    public String submitOrder(HttpServletRequest request, String addressId,String tradeCode) {

        //通过拦截器获取用户Id
        String userId = (String) request.getAttribute("userId");
        //验证交易码
        boolean b = orderService.checkTradeCode(userId,tradeCode);

        if (b){

            //通过addressId查询用户使用的收获地址
            UserAddress userAddress = userAddressService.getUserAddressById(addressId);
            //从缓存中查询出购物车的数据
            List<CartInfo> cartInfos = cartService.getCartCache(userId);

            OrderInfo orderInfo = new OrderInfo();

            //给orderInfo的属性赋值
            orderInfo.setConsignee(userAddress.getConsignee());
            orderInfo.setUserId(userId);
            orderInfo.setConsigneeTel(userAddress.getPhoneNum());
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            orderInfo.setDeliveryAddress(userAddress.getUserAddress());
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setPaymentWay(PaymentWay.ONLINE);
            orderInfo.setTotalAmount(getTotalAmount(cartInfos));
            orderInfo.setCreateTime(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            orderInfo.setExpireTime(calendar.getTime());
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = s.format(new Date());
            String tradeNo = "atguigu" + format + System.currentTimeMillis();
            orderInfo.setOutTradeNo(tradeNo);
            orderInfo.setOrderComment("火箭快递，一秒即达");
            List<OrderDetail> orderDetailList = new ArrayList<>();

            List<String> cartIds = new ArrayList<>();
            //给orderDetail的属性赋值
            for (CartInfo cartInfo : cartInfos) {
                //判断只获取购物车中被选中的
                if (cartInfo.getIsChecked().equals("1")){
                    OrderDetail orderDetail = new OrderDetail();
                    String id = cartInfo.getId();
                    cartIds.add(id);
                    orderDetail.setImgUrl(cartInfo.getImgUrl());
                    orderDetail.setSkuNum(cartInfo.getSkuNum());
                    orderDetail.setSkuName(cartInfo.getSkuName());
                    orderDetail.setSkuId(cartInfo.getSkuId());
                    //验库存
                    orderDetail.setHasStock("1");
                    //验价格
                    SkuInfo skuInfo = skuManageService.getSkuInfoById(cartInfo.getSkuId());
                    if (skuInfo.getPrice().compareTo(cartInfo.getSkuPrice())==0){
                        orderDetail.setOrderPrice(cartInfo.getCartPrice());
                    }

                    orderDetailList.add(orderDetail);
                }

            }
            orderInfo.setOrderDetailList(orderDetailList);
            //保存订单信息
            orderService.saveOrderInfo(orderInfo);
            //删除购物车中被选中的商品
            cartService.deleteCarInfo(StringUtils.join(cartIds,","),userId);

            return "redirect:http://payment.gmall.com:8077/index?outTradeNo="+tradeNo+"&totalAmount="+getTotalAmount(cartInfos);
        }else {

            return "err";
        }

    }

    /**
     * 生成结算页的方法
     * @param request
     * @param model
     * @return
     */

    @LoginRequire(autoRedirect = false)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, Model model) {

        String userId = (String) request.getAttribute("userId");
        String nickName = (String) request.getAttribute("nickName");

        List<UserAddress> userAddressList = userAddressService.getUserAddressByUserId(userId);

        List<OrderDetail> orderDetails = new ArrayList<>();

        List<CartInfo> cartInfos = cartService.getCartCache(userId);

        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            if (cartInfo.getIsChecked().equals("1")) {

                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(new BigDecimal(0));

                orderDetails.add(orderDetail);
            }
        }
        //获取订单总价
        BigDecimal totalAmount = getTotalAmount(cartInfos);
        //生成唯一交易码
        String tradeCode =  orderService.genTradeCode(userId);

        model.addAttribute("userAddressList", userAddressList);

        model.addAttribute("nickName", nickName);

        model.addAttribute("orderDetailList", orderDetails);

        model.addAttribute("totalAmount", totalAmount);

        model.addAttribute("tradeCode", tradeCode);



        return "trade";
    }

    /**
     * 计算选中商品的总价
     * @param cartInfos
     * @return
     */
    private BigDecimal getTotalAmount(List<CartInfo> cartInfos) {

        BigDecimal totalAmount = new BigDecimal(0);
        if (null != cartInfos && cartInfos.size() > 0) {

            for (CartInfo cartInfo : cartInfos) {

                if (cartInfo.getIsChecked().equals("1")) {

                    totalAmount = totalAmount.add(cartInfo.getCartPrice());
                }

            }
        }

        return totalAmount;
    }

}
