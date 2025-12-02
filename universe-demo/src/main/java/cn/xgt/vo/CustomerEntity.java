package cn.xgt.vo;
/**
 * @description TODO
 * @author XGT
 * @date 2023/12/27
 */

import lombok.Data;

/**
 * 客户信息表;
 * @author : XGT
 * @date : 2023-12-27
 */
@Data
public class CustomerEntity {
    /** 客户姓名 */
    private String name ;
    /** 证件类别。96.香港身份证,97.澳门身份证,98.台湾身份证,111.居民身份证,414.普通护照,511.台湾居民来往大陆通行证,516.港澳居民来往内地通行证,517.大陆居民往来台湾通行证,714.统一社会信用代码 */
    private String cardType ;
    /** 证件号 */
    @Mask(category = CATEGORT.ID_NUM)
    private String cardNo ;
    /** 性别。1.男，2.女 */
    private String sex ;
    /** 客户经理员工编号 */
    private String crmNo ;
    /** 客户经理姓名 */
    private String crmName ;
    /** 渠道编号 */
    private String channelNo ;
    /** 渠道客户编号 */
    private String channelCustomerNo ;


}
