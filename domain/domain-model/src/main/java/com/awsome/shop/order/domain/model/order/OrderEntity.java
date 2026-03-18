package com.awsome.shop.order.domain.model.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兑换订单领域实体
 */
@Data
public class OrderEntity {

    private Long id;

    private Long userId;

    private Long productId;

    /** 产品名称（冗余快照） */
    private String productName;

    /** 产品图片（冗余快照） */
    private String productImageUrl;

    /** 消耗积分 */
    private Integer pointsCost;

    /** 兑换状态 */
    private OrderStatus status;

    /** 积分扣除流水ID（用于取消时回滚） */
    private Long pointsTransactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 校验状态是否可以流转到目标状态
     *
     * @param target 目标状态
     * @return true 如果允许
     */
    public boolean canTransitionTo(OrderStatus target) {
        return this.status.canTransitionTo(target);
    }

    /**
     * 更新状态（强制校验状态流转合法性）
     *
     * @param target 目标状态
     * @throws IllegalStateException 如果状态流转不合法
     */
    public void transitionTo(OrderStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                    String.format("非法状态流转: %s → %s", this.status, target));
        }
        this.status = target;
    }
}
