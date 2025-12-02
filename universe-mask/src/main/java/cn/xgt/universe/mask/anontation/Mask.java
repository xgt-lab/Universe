package cn.xgt.universe.mask.anontation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import cn.xgt.universe.mask.constant.CATEGORT;
import cn.xgt.universe.mask.serializer.MaskSerializer;

import static cn.xgt.universe.mask.constant.CATEGORT.CUSTOM;

/**
 * @author XGT
 * @description TODO
 * @date 2025/11/21
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = MaskSerializer.class)
public @interface Mask {

    /** 默认打码符号 */
    String DEFAULT_ASTERISK = "*";

    /** 脱敏类型(只有类型为CUSTOM，其他字段才生效) */
    CATEGORT category() default CUSTOM;
    /** 无需打码的前置长度 */
    int prefixNoMaskLen() default 0;
    /** 无需要打码的后置长度 */
    int suffixNoMaskLen() default 0;
    /** 打码符号 */
    String asterisk() default DEFAULT_ASTERISK;

}
