package com.awsome.shop.order.application.api.client;

/**
 * 积分服务客户端接口
 *
 * <p>用于跨服务调用 points-service</p>
 */
public interface PointsServiceClient {

    /**
     * 查询积分余额
     *
     * @param userId 用户ID
     * @return 积分余额，不存在返回 null
     */
    Integer getBalance(Long userId);

    /**
     * 扣除积分
     *
     * @param userId 用户ID
     * @param amount 扣除数量
     * @param orderId 关联订单ID
     * @return 积分流水ID，失败返回 null
     */
    Long deductPoints(Long userId, int amount, Long orderId);

    /**
     * 回滚积分
     *
     * @param transactionId 积分流水ID
     * @return true 成功
     */
    boolean rollbackPoints(Long transactionId);
}
