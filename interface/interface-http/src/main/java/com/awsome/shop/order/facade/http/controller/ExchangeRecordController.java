package com.awsome.shop.order.facade.http.controller;

import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordDTO;
import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordStatsDTO;
import com.awsome.shop.order.application.api.dto.exchange.request.GetExchangeRecordRequest;
import com.awsome.shop.order.application.api.dto.exchange.request.ListExchangeRecordRequest;
import com.awsome.shop.order.application.api.service.exchange.ExchangeRecordApplicationService;
import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分兑换记录 Controller
 */
@Tag(name = "ExchangeRecord", description = "积分兑换记录管理")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExchangeRecordController {

    private final ExchangeRecordApplicationService exchangeRecordApplicationService;

    @Operation(summary = "查询兑换记录详情")
    @PostMapping("/admin/exchange-record/get")
    public Result<ExchangeRecordDTO> get(@RequestBody @Valid GetExchangeRecordRequest request) {
        return Result.success(exchangeRecordApplicationService.get(request));
    }

    @Operation(summary = "分页查询兑换记录")
    @PostMapping("/admin/exchange-record/list")
    public Result<PageResult<ExchangeRecordDTO>> list(@RequestBody @Valid ListExchangeRecordRequest request) {
        return Result.success(exchangeRecordApplicationService.list(request));
    }

    @Operation(summary = "兑换记录统计")
    @PostMapping("/admin/exchange-record/stats")
    public Result<ExchangeRecordStatsDTO> stats() {
        return Result.success(exchangeRecordApplicationService.stats());
    }
}
