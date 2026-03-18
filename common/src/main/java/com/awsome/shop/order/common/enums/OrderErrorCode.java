package com.awsome.shop.order.common.enums;

/**
 * 兑换订单业务错误码
 *
 * <p>错误码前缀决定 HTTP 状态码映射，参见 {@link ErrorCode} 接口说明。</p>
 */
public enum OrderErrorCode implements ErrorCode {

    /** 产品不存在 */
    PRODUCT_NOT_FOUND("NOT_FOUND_001", "产品不存在"),

    /** 兑换记录不存在 */
    ORDER_NOT_FOUND("NOT_FOUND_002", "兑换记录不存在"),

    /** 产品已下架 */
    PRODUCT_INACTIVE("BIZ_001", "产品已下架"),

    /** 库存不足 */
    STOCK_INSUFFICIENT("BIZ_002", "库存不足"),

    /** 积分账户不存在 */
    POINTS_ACCOUNT_NOT_FOUND("BIZ_003", "积分账户不存在"),

    /** 积分不足，无法兑换 */
    POINTS_INSUFFICIENT("BIZ_004", "积分不足，无法兑换"),

    /** 非法状态变更 */
    ILLEGAL_STATUS_TRANSITION("BIZ_005", "非法状态变更"),

    /** 无权查看此兑换记录 */
    ORDER_ACCESS_DENIED("AUTHZ_001", "无权查看此兑换记录"),

    /** 兑换处理失败，请稍后重试 */
    ORDER_PROCESS_FAILED("SYS_003", "兑换处理失败，请稍后重试"),

    /** 取消退还处理异常 */
    CANCEL_COMPENSATION_FAILED("SYS_004", "取消退还处理异常");

    private final String code;
    private final String message;

    OrderErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
