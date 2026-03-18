package com.awsome.shop.order.repository.mysql.impl.order;

import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.domain.model.order.OrderEntity;
import com.awsome.shop.order.domain.model.order.OrderStatus;
import com.awsome.shop.order.repository.mysql.mapper.order.OrderMapper;
import com.awsome.shop.order.repository.mysql.po.order.OrderPO;
import com.awsome.shop.order.repository.order.OrderRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 兑换订单仓储实现
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public OrderEntity getById(Long id) {
        OrderPO po = orderMapper.selectById(id);
        return po == null ? null : toEntity(po);
    }

    @Override
    public void save(OrderEntity entity) {
        OrderPO po = toPO(entity);
        orderMapper.insert(po);
        entity.setId(po.getId());
    }

    @Override
    public void update(OrderEntity entity) {
        OrderPO po = toPO(entity);
        orderMapper.updateById(po);
    }

    @Override
    public void deleteById(Long id) {
        orderMapper.deleteById(id);
    }

    @Override
    public PageResult<OrderEntity> pageByUserId(Long userId, int page, int size) {
        IPage<OrderPO> result = orderMapper.selectPageByUserId(new Page<>(page + 1, size), userId);
        return toPageResult(result);
    }

    @Override
    public PageResult<OrderEntity> pageAll(int page, int size, String keyword,
                                            LocalDateTime startDate, LocalDateTime endDate) {
        IPage<OrderPO> result = orderMapper.selectPageAll(new Page<>(page + 1, size), keyword, startDate, endDate);
        return toPageResult(result);
    }

    // ==================== 转换方法 ====================

    private PageResult<OrderEntity> toPageResult(IPage<OrderPO> iPage) {
        PageResult<OrderEntity> pageResult = new PageResult<>();
        pageResult.setCurrentPage(iPage.getCurrent() - 1);
        pageResult.setSize(iPage.getSize());
        pageResult.setTotalElements(iPage.getTotal());
        pageResult.setTotalPages(iPage.getPages());
        pageResult.setContent(iPage.getRecords().stream().map(this::toEntity).collect(Collectors.toList()));
        return pageResult;
    }

    private OrderEntity toEntity(OrderPO po) {
        OrderEntity entity = new OrderEntity();
        entity.setId(po.getId());
        entity.setUserId(po.getUserId());
        entity.setProductId(po.getProductId());
        entity.setProductName(po.getProductName());
        entity.setProductImageUrl(po.getProductImageUrl());
        entity.setPointsCost(po.getPointsCost());
        entity.setStatus(OrderStatus.valueOf(po.getStatus()));
        entity.setPointsTransactionId(po.getPointsTransactionId());
        entity.setCreatedAt(po.getCreatedAt());
        entity.setUpdatedAt(po.getUpdatedAt());
        return entity;
    }

    private OrderPO toPO(OrderEntity entity) {
        OrderPO po = new OrderPO();
        po.setId(entity.getId());
        po.setUserId(entity.getUserId());
        po.setProductId(entity.getProductId());
        po.setProductName(entity.getProductName());
        po.setProductImageUrl(entity.getProductImageUrl());
        po.setPointsCost(entity.getPointsCost());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        po.setPointsTransactionId(entity.getPointsTransactionId());
        return po;
    }
}
