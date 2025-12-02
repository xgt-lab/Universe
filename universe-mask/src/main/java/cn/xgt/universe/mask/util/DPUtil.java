package cn.xgt.universe.mask.util;

import org.apache.commons.lang3.StringUtils;

import static cn.xgt.universe.mask.anontation.Mask.DEFAULT_ASTERISK;

/**
 * @author XGT
 * @description TODO
 * @date 2025/11/21
 */
public class DPUtil {

    /**
     * 姓名脱敏
     *
     * @param name 姓名
     * @return 脱敏后结果
     */
    public static String nameMask(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }

        final int length = name.length();

        if (length > 9) {
            // 姓名长度大于9，显示前2后3，示例：阿里巴巴集团控股有限公司 转换后为：阿里**********有限公司
            return mask(name, 2, 3, DEFAULT_ASTERISK);
        } else if (length >= 6) {
            // 姓名长度大于等于6，小于等于9，显示前1后2，示例：阿道夫·希特勒，转换后为：阿****特勒
            return mask(name, 1, 2, DEFAULT_ASTERISK);
        } else if (length > 3) {
            // 姓名长度大于3，小于6，显示前1后1，示例：上官明月，转换后为：上****月
            return mask(name, 1, 1, DEFAULT_ASTERISK);
        } else if (length == 1) {
            // 姓名长度为1，全脱敏，示例：荒，转换后为：*
            return mask(name, 0, 0, DEFAULT_ASTERISK);
        } else {
            // 姓名长度为2或3，则显示前1，示例：李梅，转换后：李*
            return mask(name, 1, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 手机号码脱敏
     *
     * @param mobile 原字符串
     * @return 脱敏后的结果
     */
    public static String mobileMask(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return mobile;
        }

        final int length = mobile.length();

        if (length == 11) {
            // 大陆手机号码
            return mask(mobile, 3, 4, DEFAULT_ASTERISK);
        } else if (length == 13) {
            // 2位区号 + 手机号码
            return mask(mobile, 5, 4, DEFAULT_ASTERISK);
        } else if (length == 14) {
            // 3位区号 + 手机号码
            return mask(mobile, 6, 4, DEFAULT_ASTERISK);
        } else if (length >= 10) {
            return mask(mobile, 3, 4, DEFAULT_ASTERISK);
        } else if (length >= 7) {
            return mask(mobile, 2, 2, DEFAULT_ASTERISK);
        } else {
            return mask(mobile, 0, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 身份证号/护照号/通行证号等脱敏
     *
     * @param idNum 原身份证号/护照号/通行证号
     * @return 脱敏后的结果
     */
    public static String idMask(String idNum) {
        if (StringUtils.isBlank(idNum)) {
            return idNum;
        }

        final int length = idNum.length();

        if (length == 18 || length == 15) {
            return mask(idNum, 5, 2, DEFAULT_ASTERISK);
        } else if (length > 3) {
            return mask(idNum, 0, 3, DEFAULT_ASTERISK);
        } else {
            return mask(idNum, 0, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 银行卡号/银行账户号/社保号脱敏
     *
     * @param cardNum 原银行卡号/银行账户号/社保号
     * @return 脱敏后的结果
     */
    public static String cardNumMask(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return cardNum;
        }

        final int length = cardNum.length();

        if (length == 18 || length == 15) {
            return mask(cardNum, 5, 2, DEFAULT_ASTERISK);
        } else if (length > 3) {
            return mask(cardNum, 0, 3, DEFAULT_ASTERISK);
        } else {
            return mask(cardNum, 0, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 邮箱脱敏
     *
     * @param email 原字符串
     * @return 脱敏后的结果
     */
    public static String emailMask(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }

        String[] content = email.split("@");
        final int length = content[0].length();

        if (content.length > 1) {
            if (length > 5) {
                return mask(content[0], 0, 3, DEFAULT_ASTERISK) + "@" + content[1];
            } else {
                return mask(content[0], 0, 0, DEFAULT_ASTERISK) + "@" + content[1];
            }
        } else {
            if (length > 5) {
                return mask(email, 3, 0, DEFAULT_ASTERISK);
            } else {
                return mask(email, 0, 0, DEFAULT_ASTERISK);
            }
        }
    }

    /**
     * 地址脱敏
     *
     * @param address 原字符串
     * @return 脱敏后的结果
     */
    public static String addressMask(String address) {
        if (StringUtils.isBlank(address)) {
            return address;
        }

        final int length = address.length();

        if (length > 12) {
            return mask(address, 7, 0, DEFAULT_ASTERISK);
        } else if (length >= 5) {
            return mask(address, length - 5, 0, DEFAULT_ASTERISK);
        } else {
            return mask(address, 0, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 金额脱敏
     *
     * @param money 原字符串
     * @return 脱敏后的结果
     */
    public static String moneyMask(String money) {
        if (StringUtils.isBlank(money)) {
            return money;
        }

        final int length = money.length();

        if (length > 12) {
            return mask(money, 7, 0, DEFAULT_ASTERISK);
        } else if (length >= 5) {
            return mask(money, length - 5, 0, DEFAULT_ASTERISK);
        } else {
            return mask(money, 0, 0, DEFAULT_ASTERISK);
        }
    }

    /**
     * 对字符串进行脱敏操作(注意：如果prefixNoMaskLen + suffixNoMaskLen >= origin长度，则不脱敏)
     *
     * @param origin 原始字符串
     * @param prefixNoMaskLen 无需打码的前置长度
     * @param suffixNoMaskLen 无需要打码的后置长度
     * @param asterisk 用于遮罩的字符串, 默认['*']
     * @return 脱敏后结果
     */
    public static String mask(
            String origin, int prefixNoMaskLen, int suffixNoMaskLen, String asterisk) {
        if (StringUtils.isBlank(origin)) {
            return null;
        }

        StringBuilder prefixSb = new StringBuilder();
        StringBuilder suffixSb = new StringBuilder();

        int n = origin.length();
        for (int i = 0; i < n; i++) {
            if (i < prefixNoMaskLen) {
                prefixSb.append(origin.charAt(i));
                continue;
            }
            if (i > (n - suffixNoMaskLen - 1)) {
                suffixSb.append(origin.charAt(i));
            }
        }
        return prefixSb.append(asterisk).append(suffixSb).toString();
    }
}
