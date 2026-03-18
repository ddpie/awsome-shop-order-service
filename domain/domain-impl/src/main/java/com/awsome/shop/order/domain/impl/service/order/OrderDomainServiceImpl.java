package com.awsome.shop.order.domain.impl.service.order;

import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.common.enums.OrderErrorCode;
import com.awsome.shop.order.common.exception.BusinessException;
import com.awsome.shop.order.domain.model.order.OrderEntity;
import com.awsome.shop.order.domain.model.order.OrderStatus;
import com.awsome.shop.order.domain.service.order.OrderDomainService;
import com.awsome.shop.order.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 兑换订单领域服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {

    private final OrderRepository orderRepository;

    @Override
    public OrderEntity createOrder(Long userId, Long productId, String productName,
                                   String productImageUrl, Integer pointsCost) {
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userId);
        entity.setProductId(productId);
        entity.setProductName(productName);
        entity.setProductImageUrl(productImageUrl);
        entity.setPointsCost(pointsCost);
        entity.setStatus(OrderStatus.PENDING);
        orderRepository.save(entity);
        return orderRepository.getById(entity.getId());
    }

    @Override
    public void savePointsTransactionId(Long orderId, Long transactionId) {
        OrderEntity entity = getById(orderId);
        entity.setPointsTransactionId(transactionId);
        orderRepository.update(entity);
    }

    @Override
    public OrderEntity getById(Long id) {
        OrderEntity entity = orderRepository.getById(id);
        if (entity == null) {
            throw new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
        }
        return entity;
    }

    @Override
    public OrderEntity getByIdAndUserId(Long id, Long userId) {
        OrderEntity entity = getById(id);
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
        return entity;
    }

    @Override
    public PageResult<OrderEntity> pageByUserId(Long userId, int page, int size) {
        return orderRepository.pageByUserId(userId, page, size);
    }

    @Override
    public PageResult<OrderEntity> pageAll(int page, int size, String keyword,
                                            LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.pageAll(page, size, keyword, startDate, endDate);
    }

    @Override
    public OrderEntity updateStatus(Long id, OrderStatus targetStatus) {
        OrderEntity entity = getById(id);
        if (!entity.canTransitionTo(targetStatus)) {
            throw new BusinessException(OrderErrorCode.ILLEGAL_STATUS_TRANSITION);
        }
        entity.transitionTo(targetStatus);
        orderRepository.update(entity);
        return orderRepository.getById(id);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
