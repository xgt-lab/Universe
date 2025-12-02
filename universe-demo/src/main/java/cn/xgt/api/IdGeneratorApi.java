package cn.xgt.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*import cn.xgt.universe.idgenerator.IdGenerator;
import cn.xgt.universe.idgenerator.IdGeneratorFactory;*/

@RestController
@RequestMapping(value = "restful/idGenerator")
public class IdGeneratorApi {

  private Logger logger = LoggerFactory.getLogger(IdGeneratorApi.class);

  /*@Autowired
  private IdGeneratorFactory idGeneratorFactory;

  // 直接注入 Redis ID 生成器
  @Autowired
  @Qualifier("redisIdGenerator")
  private IdGenerator redisIdGenerator;

  // 直接注入 UUID ID 生成器
  @Autowired
  @Qualifier("uuidIdGenerator")
  private IdGenerator uuidIdGenerator;

  @GetMapping(value = "getInfo1")
  public void getInfo1() {
    IdGenerator uuidGenerator = idGeneratorFactory.getUuidGenerator();
    logger.info("================={}", uuidGenerator.nextIdString());
  }

  @GetMapping(value = "getInfo2")
  public void getInfo2() {
    logger.info("================={}", uuidIdGenerator.nextIdString());
  }*/
}
