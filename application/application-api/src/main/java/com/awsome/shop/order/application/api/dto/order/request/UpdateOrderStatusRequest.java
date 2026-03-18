package com.awsome.shop.order.application.api.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新兑换状态请求
 */
@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "订单ID不能为空")
    private Long id;

    @NotBlank(message = "目标状态不能为空")
    private String status;
}
