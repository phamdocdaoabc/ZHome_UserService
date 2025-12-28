package com.gfg.userservice.proxy.impl;

import com.gfg.userservice.proxy.PaymentProxy;
import com.gfg.userservice.proxy.dto.VnpayRequest;
import com.gfg.userservice.proxy.feign.PaymentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProxyImpl implements PaymentProxy {
    private final PaymentClient paymentClient;

    @Override
    public String createVnPayPayment(VnpayRequest vnpayRequest) {
        return paymentClient.createVnPayPayment(vnpayRequest).getData();
    }
}
