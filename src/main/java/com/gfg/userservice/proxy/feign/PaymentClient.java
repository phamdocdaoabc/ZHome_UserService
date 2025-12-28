package com.gfg.userservice.proxy.feign;
import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.proxy.dto.VnpayRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @PostMapping(value = "/payment-service/api/payments/vnpay", produces = "application/json")
    ApiResponse<String> createVnPayPayment(@RequestBody VnpayRequest vnpayRequest);
}
