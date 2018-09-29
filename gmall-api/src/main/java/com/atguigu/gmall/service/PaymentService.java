package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.bean.enums.PaymentStatus;

import java.util.Map;

public interface PaymentService {


    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfoByOutTradeNo(PaymentInfo paymentInfo);

    void sendPaymentResult(String outTradeNo, String tradeNo);

    void sendDelayPaymentResult(String outTradeNo, int i);

    Map<String,String> checkAlipayPayment(String outTradeNo);

    void tradeSuccessUpdatePaymentInfo(String outTradeNo, String tradeNo, String callBack);

    boolean checkPaymentInfo(String outTradeNo);
}
