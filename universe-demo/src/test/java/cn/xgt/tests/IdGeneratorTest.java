package cn.xgt.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import cn.xgt.universe.idgenerator.IdGenerator;
import cn.xgt.universe.idgenerator.IdGeneratorFactory;

/**
 * @author XGT
 * @description 具体业务测试类
 * @date 2025/12/29
 */
public class IdGeneratorTest extends UniverseTest {

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

	@Test
	public void getId1() {
		IdGenerator uuidGenerator = idGeneratorFactory.getUuidGenerator();
		String idString = uuidGenerator.nextIdString();
		logger.info("================={}", idString);
	}

	@Test
	public void getId2() {
		String idString = uuidIdGenerator.nextIdString();
		logger.info("================={}", idString);
	}


}
