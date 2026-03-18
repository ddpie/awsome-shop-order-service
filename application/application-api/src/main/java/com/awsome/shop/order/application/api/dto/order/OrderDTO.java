package com.awsome.shop.order.application.api.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兑换订单数据传输对象
 */
@Data
public class OrderDTO {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private String productImageUrl;

    private Integer pointsCost;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
