package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {


    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfoByOutTradeNo(PaymentInfo paymentInfo);

    void sendPaymentResult(String outTradeNo, String tradeNo);
}
