package com.awsome.shop.order.application.impl.service.exchange;

import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordDTO;
import com.awsome.shop.order.application.api.dto.exchange.ExchangeRecordStatsDTO;
import com.awsome.shop.order.application.api.dto.exchange.request.GetExchangeRecordRequest;
import com.awsome.shop.order.application.api.dto.exchange.request.ListExchangeRecordRequest;
import com.awsome.shop.order.application.api.service.exchange.ExchangeRecordApplicationService;
import com.awsome.shop.order.common.dto.PageResult;
import com.awsome.shop.order.domain.model.exchange.ExchangeRecordEntity;
import com.awsome.shop.order.domain.model.exchange.ExchangeRecordStatsEntity;
import com.awsome.shop.order.domain.service.exchange.ExchangeRecordDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 积分兑换记录 应用服务实现
 *
 * <p>只依赖 Domain Service，不直接依赖 Repository</p>
 */
@Service
@RequiredArgsConstructor
public class ExchangeRecordApplicationServiceImpl implements ExchangeRecordApplicationService {

    private final ExchangeRecordDomainService exchangeRecordDomainService;

    @Override
    public ExchangeRecordDTO get(GetExchangeRecordRequest request) {
        return toDTO(exchangeRecordDomainService.getById(request.getId()));
    }

    @Override
    public PageResult<ExchangeRecordDTO> list(ListExchangeRecordRequest request) {
        PageResult<ExchangeRecordEntity> page = exchangeRecordDomainService.page(
                request.getPage(), request.getSize(),
                request.getKeyword(), request.getStatus(),
                request.getStartTime(), request.getEndTime());
        return page.convert(this::toDTO);
    }

    @Override
    public ExchangeRecordStatsDTO stats() {
        ExchangeRecordStatsEntity entity = exchangeRecordDomainService.stats();
        ExchangeRecordStatsDTO dto = new ExchangeRecordStatsDTO();
        dto.setTotalCount(entity.getTotalCount());
        dto.setPendingDeliveryCount(entity.getPendingDeliveryCount());
        dto.setCompletedCount(entity.getCompletedCount());
        dto.setTotalPointsConsumed(entity.getTotalPointsConsumed());
        return dto;
    }

    private ExchangeRecordDTO toDTO(ExchangeRecordEntity entity) {
        ExchangeRecordDTO dto = new ExchangeRecordDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setProductName(entity.getProductName());
        dto.setProductDesc(entity.getProductDesc());
        dto.setEmployeeName(entity.getEmployeeName());
        dto.setPointsCost(entity.getPointsCost());
        dto.setExchangeTime(entity.getExchangeTime());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
