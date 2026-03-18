package com.awsome.shop.order.application.api.dto.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建兑换订单请求
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "产品ID不能为空")
    @Min(value = 1, message = "产品ID必须大于0")
    private Long productId;

    /** 用户ID（由网关注入，不在请求体中传递） */
    private Long operatorId;
}
