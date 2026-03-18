package com.awsome.shop.order.repository.mysql.mapper.order;

import com.awsome.shop.order.repository.mysql.po.order.OrderPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 兑换订单 Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {

    /**
     * 按用户ID分页查询订单
     */
    IPage<OrderPO> selectPageByUserId(IPage<OrderPO> page, @Param("userId") Long userId);

    /**
     * 管理员分页查询所有订单
     */
    IPage<OrderPO> selectPageAll(IPage<OrderPO> page,
                                  @Param("keyword") String keyword,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
}
