package com.awsome.shop.order.repository.order;

import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.domain.model.order.OrderEntity;

import java.time.LocalDateTime;

/**
 * 兑换订单仓储接口（Port）
 */
public interface OrderRepository {

    OrderEntity getById(Long id);

    void save(OrderEntity entity);

    void update(OrderEntity entity);

    void deleteById(Long id);

    /**
     * 按用户ID分页查询订单
     */
    PageResult<OrderEntity> pageByUserId(Long userId, int page, int size);

    /**
     * 管理员分页查询所有订单
     */
    PageResult<OrderEntity> pageAll(int page, int size, String keyword,
                                     LocalDateTime startDate, LocalDateTime endDate);
}
