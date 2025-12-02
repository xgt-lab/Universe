package cn.xgt.universe.mask.constant;


import lombok.Getter;

/**
 * @author XGT
 * @description 脱敏策略
 * @date 2025/11/21
 */
@Getter
public enum CATEGORT {
    /**
     * 自定义
     */
    CUSTOM,

    /**
     * 名称、姓名等
     */
    NAME,

    /**
     * 手机号
     */
    MOBILE,

    /**
     * 身份证号/护照号/通行证号等
     */
    ID_NUM,

    /**
     * 银行卡号/银行账户号/社保号等
     */
    CARD_NUM,

    /**
     * 电子邮箱
     */
    EMAIL,

    /**
     * 地址
     */
    ADDRESS,

    /**
     * 金额
     */
    MONEY
}
