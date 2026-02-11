package com.awsome.shop.order.application.api.service.exchange;

import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordDTO;
import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordStatsDTO;
import com.awsome.shop.order.application.api.dto.exchange.request.GetExchangeRecordRequest;
import com.awsome.shop.order.application.api.dto.exchange.request.ListExchangeRecordRequest;
import com.awsome.shop.order.common.dto.PageResult;

/**
 * 积分兑换记录 应用服务接口
 */
public interface ExchangeRecordApplicationService {

    ExchangeRecordDTO get(GetExchangeRecordRequest request);

    PageResult<ExchangeRecordDTO> list(ListExchangeRecordRequest request);

    ExchangeRecordStatsDTO stats();
}
