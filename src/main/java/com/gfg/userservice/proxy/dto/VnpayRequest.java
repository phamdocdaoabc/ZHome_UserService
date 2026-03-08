package com.gfg.userservice.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VnpayRequest {
    private Double amount;
    // ID của post_package_map (nếu có)
    private Long postPackageMapId;
    // Mô tả đơn hàng
    private String orderInfo;
    // Bank code (optional - để trống nếu muốn chọn ngân hàng trên trang VNPAY)
    private String bankCode;
}
