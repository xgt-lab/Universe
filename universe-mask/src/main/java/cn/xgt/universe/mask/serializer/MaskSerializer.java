package cn.xgt.universe.mask.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Objects;

import cn.xgt.universe.mask.anontation.Mask;
import cn.xgt.universe.mask.util.DPUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author XGT
 * @description 脱敏序列化处理器
 * @date 2025/11/21
 */
@Order(value = 0)
@NoArgsConstructor
@AllArgsConstructor
public class MaskSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private Mask mask;
    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value       Value to serialize; can <b>not</b> be null.
     * @param gen         Generator used to output resulting Json content
     * @param serializers Provider that can be used to get serializers for
     *                    serializing Objects value contains, if any.
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        switch (mask.category()) {
            case CUSTOM:
                gen.writeString(DPUtil.mask(value, mask.prefixNoMaskLen(), mask.suffixNoMaskLen(), mask.asterisk()));
                break;
            case NAME:
                gen.writeString(DPUtil.nameMask(value));
                break;
            case MOBILE:
                gen.writeString(DPUtil.mobileMask(value));
                break;
            case ID_NUM:gen.writeString(DPUtil.idMask(value));
                break;
            case CARD_NUM:
                gen.writeString(DPUtil.cardNumMask(value));
                break;
            case EMAIL:
                gen.writeString(DPUtil.emailMask(value));
                break;
            case ADDRESS:
                gen.writeString(DPUtil.addressMask(value));
                break;
            case MONEY:
                gen.writeString(DPUtil.moneyMask(value));
                break;
            default:
                throw new RuntimeException("未知脱敏策略");
        }
    }

    /**
     * Method called to see if a different (or differently configured) serializer
     * is needed to serialize values of specified property.
     * Note that instance that this method is called on is typically shared one and
     * as a result method should <b>NOT</b> modify this instance but rather construct
     * and return a new instance. This instance should only be returned as-is, in case
     * it is already suitable for use.
     *
     * @param prov     Serializer provider to use for accessing config, other serializers
     * @param property Method or field that represents the property
     *                 (and is used to access value to serialize).
     *                 Should be available; but there may be cases where caller cannot provide it and
     *                 null is passed instead (in which case impls usually pass 'this' serializer as is)
     * @return Serializer to use for serializing values of specified property;
     * may be this instance or a new instance.
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            // 类型检查：MaskSerializer 只处理 String 类型
            if (Objects.equals(property.getType().getRawClass(), String.class)) {
                // 先尝试直接获取注解
                Mask mask = property.getAnnotation(Mask.class);
                // 如果获取不到，尝试从上下文获取（支持 @JacksonAnnotationsInside）
                if (mask == null) {
                    mask = property.getContextAnnotation(Mask.class);
                }
                // 如果找到了 Mask 注解，创建新的序列化器实例
                if (mask != null) {
                    return new MaskSerializer(mask);
                }
            }
            // 如果没有 Mask 注解，使用默认的序列化器
            return prov.findValueSerializer(property.getType(), property);
        }
        // property 为 null 时，返回 null 值序列化器
        return prov.findNullValueSerializer(null);
    }
}
