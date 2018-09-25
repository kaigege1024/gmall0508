package com.atguigu.gmall.payment.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
@Controller
public class PaymentController {

    @Reference
    OrderService orderService;

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;


    @RequestMapping("alipay/callback/return")
    public String alipayReturn(HttpServletRequest request){

        try {
            boolean b = AlipaySignature.rsaCheckV1(null, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
            if (b){
                //验证成功
            }else {
                //验证失败
            }
        } catch (Exception e) {
            System.err.println("验证阿里的签名");
        }

        String outTradeNo = request.getParameter("out_trade_no");

        String callBack = request.getQueryString();
        String tradeNo = request.getParameter("trade_no");

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(outTradeNo);

        paymentInfo.setAlipayTradeNo(tradeNo);

        paymentInfo.setCallbackTime(new Date());

        paymentInfo.setPaymentStatus("已支付");

        paymentInfo.setCallbackContent(callBack);
        //更新paymentInfo表
        paymentService.updatePaymentInfoByOutTradeNo(paymentInfo);
        //创建一个队列消息
        paymentService.sendPaymentResult(outTradeNo,tradeNo);


        return  "finish";

    }






    @LoginRequire(autoRedirect = false)
    @RequestMapping("index")
    public String index(HttpServletRequest request, String outTradeNo, String totalAmount, Model model){

        String nickName = (String) request.getAttribute("nickName");

        model.addAttribute("outTradeNo",outTradeNo);

        model.addAttribute("totalAmount",totalAmount);

        model.addAttribute("nickName",nickName);


        return "index";
    }
    @LoginRequire(autoRedirect = false)
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipay(String outTradeNo,String totalAmount ){

     OrderInfo orderInfo =  orderService.getOrderInfoByTradeNo(outTradeNo);

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        HashMap<String, Object> map = new HashMap<>();

        map.put("out_trade_no",outTradeNo);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);
        map.put("subject",orderInfo.getOrderDetailList().get(0).getSkuName());
        map.put("body","测试手机");

        String s = JSON.toJSONString(map);
        alipayRequest.setBizContent(s);
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(orderInfo.getOrderDetailList().get(0).getSkuName());

        paymentService.savePaymentInfo(paymentInfo);


        return form;
    }
}
