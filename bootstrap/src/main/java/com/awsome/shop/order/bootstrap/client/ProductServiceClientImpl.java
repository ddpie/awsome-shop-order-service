package com.awsome.shop.order.bootstrap.client;

import com.awsome.shop.order.application.api.client.ProductServiceClient;
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
 * 产品服务客户端实现
 *
 * <p>通过 HTTP 调用 product-service 内部接口，支持超时/网络异常自动重试 1 次</p>
 */
@Slf4j
@Component
public class ProductServiceClientImpl implements ProductServiceClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductServiceClientImpl(
            RestTemplate restTemplate,
            @Value("${service.product.url:http://localhost:8002}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProductInfo getProduct(Long productId) {
        String url = productServiceUrl + "/api/v1/internal/product/get";
        Map<String, Object> body = Map.of("id", productId);

        Map<String, Object> responseBody = postWithRetry(url, body, "查询产品");
        if (responseBody == null || !"SUCCESS".equals(responseBody.get("code"))) {
            log.warn("[ProductClient] 查询产品失败, productId={}, response={}", productId, responseBody);
            return null;
        }

        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        if (data == null) return null;

        // Product service returns status as Integer (0=ACTIVE, 1=OFF_SHELF)
        // Convert to String for order service compatibility
        Object statusObj = data.get("status");
        String statusStr;
        if (statusObj instanceof Number) {
            statusStr = ((Number) statusObj).intValue() == 0 ? "ACTIVE" : "OFF_SHELF";
        } else {
            statusStr = statusObj != null ? statusObj.toString() : null;
        }

        return new ProductInfo(
                toLong(data.get("id")),
                (String) data.get("name"),
                (String) data.get("mainImage"),
                toIntRounded(data.get("pointsPrice")),
                toInt(data.get("stock")),
                statusStr
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean deductStock(Long productId, int quantity) {
        String url = productServiceUrl + "/api/v1/internal/product/deduct-stock";
        Map<String, Object> body = Map.of("productId", productId, "quantity", quantity);

        Map<String, Object> responseBody = postWithRetry(url, body, "扣减库存");
        return responseBody != null && "SUCCESS".equals(responseBody.get("code"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean restoreStock(Long productId, int quantity) {
        String url = productServiceUrl + "/api/v1/internal/product/restore-stock";
        Map<String, Object> body = Map.of("productId", productId, "quantity", quantity);

        Map<String, Object> responseBody = postWithRetry(url, body, "恢复库存");
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
            log.warn("[ProductClient] {} 超时/网络异常，立即重试, url={}", operation, url, e);
            try {
                return doPost(url, body);
            } catch (Exception retryEx) {
                log.error("[ProductClient] {} 重试仍失败, url={}", operation, url, retryEx);
                return null;
            }
        } catch (Exception e) {
            log.error("[ProductClient] {} 异常, url={}", operation, url, e);
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

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private Integer toIntRounded(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return (int) Math.round(((Number) value).doubleValue());
        return (int) Math.round(Double.parseDouble(value.toString()));
    }
}
