package com.awsome.shop.order.repository.mysql.po.exchange;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分兑换记录 持久化对象
 */
@Data
@TableName("exchange_record")
public class ExchangeRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String productName;

    private String productDesc;

    private String employeeName;

    private Integer pointsCost;

    private LocalDateTime exchangeTime;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
