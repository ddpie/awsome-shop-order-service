package com.awsome.shop.order.bootstrap.client;

import com.awsome.shop.order.application.api.client.PointsServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 积分服务客户端实现
 *
 * <p>通过 HTTP 调用 points-service 内部接口，支持超时/网络异常自动重试 1 次</p>
 */
@Slf4j
@Component
public class PointsServiceClientImpl implements PointsServiceClient {

    private final RestTemplate restTemplate;
    private final String pointsServiceUrl;

    public PointsServiceClientImpl(
            RestTemplate restTemplate,
            @Value("${service.points.url:http://localhost:8003}") String pointsServiceUrl) {
        this.restTemplate = restTemplate;
        this.pointsServiceUrl = pointsServiceUrl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Integer getBalance(Long userId) {
        String url = pointsServiceUrl + "/api/v1/internal/point/balance";
        Map<String, Object> body = Map.of("userId", userId);

        Map<String, Object> responseBody = postWithRetry(url, body, "查询余额");
        if (responseBody == null || !"SUCCESS".equals(responseBody.get("code"))) {
            log.warn("[PointsClient] 查询余额失败, userId={}, response={}", userId, responseBody);
            return null;
        }

        Object data = responseBody.get("data");
        if (data == null) return null;
        if (data instanceof Map) {
            Object balance = ((Map<String, Object>) data).get("balance");
            return balance != null ? ((Number) balance).intValue() : null;
        }
        return ((Number) data).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long deductPoints(Long userId, int amount, Long orderId) {
        String url = pointsServiceUrl + "/api/v1/internal/point/deduct";
        Map<String, Object> body = Map.of("userId", userId, "amount", amount, "orderId", orderId);

        Map<String, Object> responseBody = postWithRetry(url, body, "积分扣除");
        if (responseBody == null || !"SUCCESS".equals(responseBody.get("code"))) {
            log.warn("[PointsClient] 积分扣除失败, userId={}, amount={}, response={}", userId, amount, responseBody);
            return null;
        }

        Object data = responseBody.get("data");
        if (data == null) return null;
        if (data instanceof Map) {
            // Points service returns "id" as the transaction ID field
            Object transactionId = ((Map<String, Object>) data).get("id");
            return transactionId != null ? ((Number) transactionId).longValue() : null;
        }
        return ((Number) data).longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean rollbackPoints(Long transactionId) {
        String url = pointsServiceUrl + "/api/v1/internal/point/rollback";
        Map<String, Object> body = Map.of("transactionId", transactionId);

        Map<String, Object> responseBody = postWithRetry(url, body, "积分回滚");
        return responseBody != null && "SUCCESS".equals(responseBody.get("code"));
    }

    // ==================== 重试与工具方法 ====================

    /**
     * 带重试的 POST 请求（超时/网络异常自动重试 1 次）
     *
     * @param url       请求地址
     * @param body      请求体
     * @param operation 操作描述（用于日志）
     * @return 响应体 Map，失败返回 null
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> postWithRetry(String url, Map<String, Object> body, String operation) {
        try {
            return doPost(url, body);
        } catch (ResourceAccessException e) {
            // 超时或网络异常，立即重试 1 次
            log.warn("[PointsClient] {} 超时/网络异常，立即重试, url={}", operation, url, e);
            try {
                return doPost(url, body);
            } catch (Exception retryEx) {
                log.error("[PointsClient] {} 重试仍失败, url={}", operation, url, retryEx);
                return null;
            }
        } catch (Exception e) {
            log.error("[PointsClient] {} 异常, url={}", operation, url, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> doPost(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody();
    }
}
