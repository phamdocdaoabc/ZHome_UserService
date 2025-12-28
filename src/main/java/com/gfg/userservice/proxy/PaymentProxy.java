package com.gfg.userservice.proxy;


import com.gfg.userservice.proxy.dto.VnpayRequest;

public interface PaymentProxy {
   String createVnPayPayment(VnpayRequest vnpayRequest);
}
