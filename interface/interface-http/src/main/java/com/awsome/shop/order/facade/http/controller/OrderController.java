package com.awsome.shop.order.facade.http.controller;

import com.awsome.shop.order.application.api.dto.order.OrderDTO;
import com.awsome.shop.order.application.api.dto.order.request.*;
import com.awsome.shop.order.application.api.service.order.OrderApplicationService;
import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.common.enums.OrderErrorCode;
import com.awsome.shop.order.common.exception.BusinessException;
import com.awsome.shop.order.facade.http.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 兑换订单 Controller
 *
 * <p>员工端点（public）：创建兑换、查询历史、查询详情</p>
 * <p>管理员端点（private）：查看所有记录、更新状态</p>
 */
@Tag(name = "兑换订单", description = "兑换订单管理接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    // ==================== 员工端点 ====================

    @Operation(summary = "创建兑换订单")
    @PostMapping("/public/order/create")
    public Result<OrderDTO> create(
            @RequestHeader(value = "X-Operator-Id") Long operatorId,
            @RequestBody @Valid CreateOrderRequest request) {
        validateOperatorId(operatorId);
        request.setOperatorId(operatorId);
        return Result.success(orderApplicationService.create(request));
    }

    @Operation(summary = "查询兑换详情")
    @PostMapping("/public/order/get")
    public Result<OrderDTO> get(
            @RequestHeader(value = "X-Operator-Id") Long operatorId,
            @RequestBody @Valid GetOrderRequest request) {
        validateOperatorId(operatorId);
        request.setOperatorId(operatorId);
        return Result.success(orderApplicationService.get(request));
    }

    @Operation(summary = "查询当前用户兑换历史")
    @PostMapping("/public/order/list")
    public Result<PageResult<OrderDTO>> list(
            @RequestHeader(value = "X-Operator-Id") Long operatorId,
            @RequestBody @Valid ListOrderRequest request) {
        validateOperatorId(operatorId);
        request.setOperatorId(operatorId);
        return Result.success(orderApplicationService.list(request));
    }

    // ==================== 管理员端点（仅内部网络可访问） ====================

    @Operation(summary = "管理员查看所有兑换记录")
    @PostMapping("/private/order/admin/list")
    public Result<PageResult<OrderDTO>> adminList(
            @RequestBody @Valid AdminListOrderRequest request) {
        return Result.success(orderApplicationService.adminList(request));
    }

    @Operation(summary = "管理员更新兑换状态")
    @PostMapping("/private/order/admin/update-status")
    public Result<OrderDTO> adminUpdateStatus(
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        return Result.success(orderApplicationService.adminUpdateStatus(request));
    }

    // ==================== 工具方法 ====================

    private void validateOperatorId(Long operatorId) {
        if (operatorId == null) {
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }
}
