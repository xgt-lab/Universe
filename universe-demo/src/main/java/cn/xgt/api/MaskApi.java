package cn.xgt.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.xgt.vo.CustomerEntity;

/**
 * @author XGT
 * @description Mask Test
 * @date 2025/12/3
 */
@RestController
@RequestMapping(value = "restful/mask")
public class MaskApi {

  private Logger logger = LoggerFactory.getLogger(MaskApi.class);

  /**
   * 使用@Mask注解自动脱敏（返回JSON时自动脱敏）
   *
   * eg:
   * {
   *   "name": "张三李四王五",
   *   "mobile": "13800138000",
   *   "cardNo": "110112200801010739",
   *   "bankCardNo": "6222807728905421317",
   *   "email": "zhangsan@example.com",
   *   "address": "北京市朝阳区建国路001号",
   *   "money": "1234567890",
   *   "customField": "张三李四王五",
   *   "normalField": "张三李四王五"
   * }
   */
  @PostMapping(value = "getInfo1")
  public CustomerEntity getInfo1(@RequestBody Map<String, Object> param) {
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setName(MapUtil.getStr(param, CustomerEntity.Fields.name));
    customerEntity.setMobile(MapUtil.getStr(param, CustomerEntity.Fields.mobile));
    customerEntity.setCardNo(MapUtil.getStr(param, CustomerEntity.Fields.cardNo));
    customerEntity.setBankCardNo(MapUtil.getStr(param, CustomerEntity.Fields.bankCardNo));
    customerEntity.setEmail(MapUtil.getStr(param, CustomerEntity.Fields.email));
    customerEntity.setAddress(MapUtil.getStr(param, CustomerEntity.Fields.address));
    customerEntity.setMoney(MapUtil.getStr(param, CustomerEntity.Fields.money));
    customerEntity.setCustomField(MapUtil.getStr(param, CustomerEntity.Fields.customField));
    customerEntity.setNormalField(MapUtil.getStr(param, CustomerEntity.Fields.normalField));

    logger.info("========================customerEntity:{}", JSONUtil.toJsonStr(customerEntity));
    return customerEntity;
  }
}
