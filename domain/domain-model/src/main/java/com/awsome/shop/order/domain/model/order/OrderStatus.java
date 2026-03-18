package com.awsome.shop.order.domain.model.order;

/**
 * 兑换订单状态枚举
 *
 * <p>状态流转：</p>
 * <pre>
 * PENDING → READY → COMPLETED
 *   │         │
 *   └─────────┴──→ CANCELLED
 * </pre>
 */
public enum OrderStatus {

    /** 已兑换，等待自取 */
    PENDING,

    /** 可自取 */
    READY,

    /** 已完成（终态） */
    COMPLETED,

    /** 已取消（终态） */
    CANCELLED;

    /**
     * 判断是否可以流转到目标状态
     *
     * @param target 目标状态
     * @return true 如果允许流转
     */
    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING -> target == READY || target == CANCELLED;
            case READY -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
