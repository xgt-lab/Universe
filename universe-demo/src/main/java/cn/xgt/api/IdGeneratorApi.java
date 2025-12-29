package cn.xgt.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.xgt.universe.idgenerator.IdGenerator;
import cn.xgt.universe.idgenerator.IdGeneratorFactory;

/**
 * @author XGT
 * @description IdGenerator Test
 * @date 2025/12/3
 */
@RestController
@RequestMapping(value = "restful/idGenerator")
public class IdGeneratorApi {

  private Logger logger = LoggerFactory.getLogger(IdGeneratorApi.class);

  // 方式一：通过工厂类获取（推荐）
  @Autowired
  private IdGeneratorFactory idGeneratorFactory;

  // 方式二：直接注入
  // 直接注入 Redis ID 生成器
  @Autowired
  @Qualifier("redisIdGenerator")
  private IdGenerator redisIdGenerator;

  // 直接注入 UUID ID 生成器
  @Autowired
  @Qualifier("uuidIdGenerator")
  private IdGenerator uuidIdGenerator;

  @GetMapping(value = "getId1")
  public String getId1() {
    IdGenerator uuidGenerator = idGeneratorFactory.getUuidGenerator();
    String idString = uuidGenerator.nextIdString();
    logger.info("================={}", idString);
    return idString;
  }

  @GetMapping(value = "getId2")
  public String getId2() {
    String idString = uuidIdGenerator.nextIdString();
    logger.info("================={}", idString);
    return idString;
  }
}
