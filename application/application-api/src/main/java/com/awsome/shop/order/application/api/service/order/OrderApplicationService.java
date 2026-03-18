package com.awsome.shop.order.application.api.service.order;

import com.awsome.shop.order.application.api.dto.order.OrderDTO;
import com.awsome.shop.order.application.api.dto.order.request.*;
import com.awsome.shop.order.common.dto.PageResult;

/**
 * 兑换订单应用服务接口
 */
public interface OrderApplicationService {

    /**
     * 创建兑换订单（Saga 编排）
     */
    OrderDTO create(CreateOrderRequest request);

    /**
     * 查询兑换详情（校验归属）
     */
    OrderDTO get(GetOrderRequest request);

    /**
     * 查询当前用户兑换历史
     */
    PageResult<OrderDTO> list(ListOrderRequest request);

    /**
     * 管理员查看所有兑换记录
     */
    PageResult<OrderDTO> adminList(AdminListOrderRequest request);

    /**
     * 管理员更新兑换状态（含取消补偿）
     */
    OrderDTO adminUpdateStatus(UpdateOrderStatusRequest request);
}
