package com.awsome.shop.order.domain.service.order;

import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.domain.model.order.OrderEntity;
import com.awsome.shop.order.domain.model.order.OrderStatus;

import java.time.LocalDateTime;

/**
 * 兑换订单领域服务接口
 */
public interface OrderDomainService {

    /**
     * 创建 PENDING 状态的订单记录
     */
    OrderEntity createOrder(Long userId, Long productId, String productName,
                            String productImageUrl, Integer pointsCost);

    /**
     * 保存积分流水ID到订单
     */
    void savePointsTransactionId(Long orderId, Long transactionId);

    /**
     * 根据ID查询订单
     */
    OrderEntity getById(Long id);

    /**
     * 根据ID查询订单并校验归属
     */
    OrderEntity getByIdAndUserId(Long id, Long userId);

    /**
     * 按用户ID分页查询
     */
    PageResult<OrderEntity> pageByUserId(Long userId, int page, int size);

    /**
     * 管理员分页查询
     */
    PageResult<OrderEntity> pageAll(int page, int size, String keyword,
                                     LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 更新订单状态（含状态流转校验）
     */
    OrderEntity updateStatus(Long id, OrderStatus targetStatus);

    /**
     * 删除订单（Saga 补偿用）
     */
    void deleteById(Long id);
}
