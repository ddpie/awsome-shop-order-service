package com.awsome.shop.order.repository.exchange;

import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.domain.model.exchange.ExchangeRecordEntity;
import com.awsome.shop.order.domain.model.exchange.ExchangeRecordStatsEntity;

import java.time.LocalDateTime;

/**
 * 积分兑换记录 仓储接口
 */
public interface ExchangeRecordRepository {

    ExchangeRecordEntity getById(Long id);

    PageResult<ExchangeRecordEntity> page(int page, int size, String keyword, String status,
                                          LocalDateTime startTime, LocalDateTime endTime);

    ExchangeRecordStatsEntity stats();
}
