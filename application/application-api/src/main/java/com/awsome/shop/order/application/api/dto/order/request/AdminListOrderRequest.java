package com.awsome.shop.order.application.api.dto.order.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员查看所有兑换记录请求
 */
@Data
public class AdminListOrderRequest {

    @Min(value = 1, message = "页码最小为 1")
    private Integer page = 1;

    @Min(value = 1, message = "每页大小最小为 1")
    @Max(value = 100, message = "每页大小最大为 100")
    private Integer size = 20;

    /** 按产品名称搜索 */
    private String keyword;

    /** 开始时间 */
    private LocalDateTime startDate;

    /** 结束时间 */
    private LocalDateTime endDate;
}
