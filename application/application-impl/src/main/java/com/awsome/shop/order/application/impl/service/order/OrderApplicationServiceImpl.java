package com.awsome.shop.order.application.impl.service.order;

import com.awsome.shop.order.application.api.client.PointsServiceClient;
import com.awsome.shop.order.application.api.client.ProductServiceClient;
import com.awsome.shop.order.application.api.client.ProductServiceClient.ProductInfo;
import com.awsome.shop.order.application.api.dto.order.OrderDTO;
import com.awsome.shop.order.application.api.dto.order.request.*;
import com.awsome.shop.order.application.api.service.order.OrderApplicationService;
import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.common.enums.OrderErrorCode;
import com.awsome.shop.order.common.exception.BusinessException;
import com.awsome.shop.order.common.exception.SystemException;
import com.awsome.shop.order.domain.model.order.OrderEntity;
import com.awsome.shop.order.domain.model.order.OrderStatus;
import com.awsome.shop.order.domain.service.order.OrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 兑换订单应用服务实现
 *
 * <p>编排领域服务 + 跨服务调用，实现 Saga 最大努力补偿模式</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderDomainService orderDomainService;
    private final ProductServiceClient productServiceClient;
    private final PointsServiceClient pointsServiceClient;

    @Override
    @Transactional
    public OrderDTO create(CreateOrderRequest request) {
        Long userId = request.getOperatorId();
        Long productId = request.getProductId();

        // === 阶段一：预校验（不加锁） ===
        ProductInfo product = productServiceClient.getProduct(productId);
        if (product == null) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }
        if (!"ACTIVE".equals(product.status())) {
            throw new BusinessException(OrderErrorCode.PRODUCT_INACTIVE);
        }
        if (product.stock() == null || product.stock() <= 0) {
            throw new BusinessException(OrderErrorCode.STOCK_INSUFFICIENT);
        }

        Integer balance = pointsServiceClient.getBalance(userId);
        if (balance == null) {
            throw new BusinessException(OrderErrorCode.POINTS_ACCOUNT_NOT_FOUND);
        }
        if (balance < product.pointsPrice()) {
            throw new BusinessException(OrderErrorCode.POINTS_INSUFFICIENT);
        }

        // === 阶段二：创建 PENDING 订单 ===
        OrderEntity order = orderDomainService.createOrder(
                userId, productId, product.name(), product.imageUrl(), product.pointsPrice());

        // === 阶段三：Saga 执行 — 先积分后库存 ===
        // Saga 步骤 1：扣除积分
        Long transactionId;
        try {
            transactionId = pointsServiceClient.deductPoints(userId, product.pointsPrice(), order.getId());
            if (transactionId == null) {
                orderDomainService.deleteById(order.getId());
                throw new BusinessException(OrderErrorCode.POINTS_INSUFFICIENT);
            }
        } catch (BusinessException e) {
            orderDomainService.deleteById(order.getId());
            throw e;
        } catch (Exception e) {
            log.error("[兑换] 积分扣除异常, orderId={}, userId={}", order.getId(), userId, e);
            orderDomainService.deleteById(order.getId());
            throw new SystemException(OrderErrorCode.ORDER_PROCESS_FAILED, e);
        }

        // 保存积分流水ID
        orderDomainService.savePointsTransactionId(order.getId(), transactionId);

        // Saga 步骤 2：扣减库存
        try {
            boolean stockDeducted = productServiceClient.deductStock(productId, 1);
            if (!stockDeducted) {
                // 补偿：回滚积分 + 删除订单
                compensatePointsRollback(transactionId, order.getId());
                orderDomainService.deleteById(order.getId());
                throw new BusinessException(OrderErrorCode.STOCK_INSUFFICIENT);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[兑换] 库存扣减异常, orderId={}, productId={}", order.getId(), productId, e);
            compensatePointsRollback(transactionId, order.getId());
            orderDomainService.deleteById(order.getId());
            throw new SystemException(OrderErrorCode.ORDER_PROCESS_FAILED, e);
        }

        return toDTO(orderDomainService.getById(order.getId()));
    }

    @Override
    public OrderDTO get(GetOrderRequest request) {
        OrderEntity entity = orderDomainService.getByIdAndUserId(request.getId(), request.getOperatorId());
        return toDTO(entity);
    }

    @Override
    public PageResult<OrderDTO> list(ListOrderRequest request) {
        PageResult<OrderEntity> page = orderDomainService.pageByUserId(
                request.getOperatorId(), request.getPage(), request.getSize());
        return page.convert(this::toDTO);
    }

    @Override
    public PageResult<OrderDTO> adminList(AdminListOrderRequest request) {
        PageResult<OrderEntity> page = orderDomainService.pageAll(
                request.getPage(), request.getSize(),
                request.getKeyword(), request.getStartDate(), request.getEndDate());
        return page.convert(this::toDTO);
    }

    @Override
    @Transactional
    public OrderDTO adminUpdateStatus(UpdateOrderStatusRequest request) {
        OrderStatus targetStatus;
        try {
            targetStatus = OrderStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(OrderErrorCode.ILLEGAL_STATUS_TRANSITION);
        }

        // 如果目标状态是 CANCELLED，先获取订单信息用于补偿
        if (targetStatus == OrderStatus.CANCELLED) {
            OrderEntity order = orderDomainService.getById(request.getId());

            // 更新状态（含流转校验）
            OrderEntity updated = orderDomainService.updateStatus(request.getId(), targetStatus);

            // Saga 补偿：回滚积分 + 恢复库存（最大努力）
            compensateCancellation(order);

            return toDTO(updated);
        }

        // 非取消状态，直接更新
        OrderEntity updated = orderDomainService.updateStatus(request.getId(), targetStatus);
        return toDTO(updated);
    }

    // ==================== Saga 补偿方法 ====================

    /**
     * 积分回滚补偿（最大努力）
     */
    private void compensatePointsRollback(Long transactionId, Long orderId) {
        try {
            boolean success = pointsServiceClient.rollbackPoints(transactionId);
            if (!success) {
                log.error("[Saga补偿] 积分回滚失败, transactionId={}, orderId={}, 需人工介入",
                        transactionId, orderId);
            }
        } catch (Exception e) {
            log.error("[Saga补偿] 积分回滚异常, transactionId={}, orderId={}, 需人工介入",
                    transactionId, orderId, e);
        }
    }

    /**
     * 取消订单补偿：回滚积分 + 恢复库存（最大努力）
     */
    private void compensateCancellation(OrderEntity order) {
        // 回滚积分
        if (order.getPointsTransactionId() != null) {
            try {
                boolean success = pointsServiceClient.rollbackPoints(order.getPointsTransactionId());
                if (!success) {
                    log.error("[取消补偿] 积分回滚失败, orderId={}, transactionId={}, 需人工介入",
                            order.getId(), order.getPointsTransactionId());
                }
            } catch (Exception e) {
                log.error("[取消补偿] 积分回滚异常, orderId={}, transactionId={}, 需人工介入",
                        order.getId(), order.getPointsTransactionId(), e);
            }
        }

        // 恢复库存
        try {
            boolean success = productServiceClient.restoreStock(order.getProductId(), 1);
            if (!success) {
                log.error("[取消补偿] 库存恢复失败, orderId={}, productId={}, 需人工介入",
                        order.getId(), order.getProductId());
            }
        } catch (Exception e) {
            log.error("[取消补偿] 库存恢复异常, orderId={}, productId={}, 需人工介入",
                    order.getId(), order.getProductId(), e);
        }
    }

    // ==================== 转换方法 ====================

    private OrderDTO toDTO(OrderEntity entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductImageUrl(entity.getProductImageUrl());
        dto.setPointsCost(entity.getPointsCost());
        dto.setStatus(entity.getStatus().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
