package com.awsome.shop.order.application.api.client;

/**
 * 产品服务客户端接口
 *
 * <p>用于跨服务调用 product-service</p>
 */
public interface ProductServiceClient {

    /**
     * 查询产品信息
     *
     * @param productId 产品ID
     * @return 产品信息，不存在返回 null
     */
    ProductInfo getProduct(Long productId);

    /**
     * 扣减库存
     *
     * @param productId 产品ID
     * @param quantity 数量
     * @return true 成功
     */
    boolean deductStock(Long productId, int quantity);

    /**
     * 恢复库存
     *
     * @param productId 产品ID
     * @param quantity 数量
     * @return true 成功
     */
    boolean restoreStock(Long productId, int quantity);

    /**
     * 产品信息
     */
    record ProductInfo(
            Long id,
            String name,
            String imageUrl,
            Integer pointsPrice,
            Integer stock,
            String status
    ) {}
}
