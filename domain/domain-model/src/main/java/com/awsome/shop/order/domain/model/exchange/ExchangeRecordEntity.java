package com.awsome.shop.order.domain.model.exchange;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分兑换记录 领域实体
 */
@Data
public class ExchangeRecordEntity {

    private Long id;
    private String orderNo;
    private String productName;
    private String productDesc;
    private String employeeName;
    private Integer pointsCost;
    private LocalDateTime exchangeTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
