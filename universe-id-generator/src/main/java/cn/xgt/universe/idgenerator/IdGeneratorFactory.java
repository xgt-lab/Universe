package cn.xgt.universe.idgenerator;

/**
 * @author XGT
 * @description TODO
 * @date 2025/11/5
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

/**
 * @author XGT
 * @description ID生成器工厂类，用于获取不同的ID生成器
 * @date 2025/11/5
 */
@Component
public class IdGeneratorFactory {

	Logger logger = LoggerFactory.getLogger(IdGeneratorFactory.class);

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, IdGenerator> generatorMap = new HashMap<>();

	@PostConstruct
	public void init() {
		// 自动注册所有 IdGenerator 类型的 Bean
		Map<String, IdGenerator> generators = applicationContext.getBeansOfType(IdGenerator.class);
		generatorMap.putAll(generators);
		logger.info("已注册ID生成器: {}", generatorMap.keySet());
	}

	/**
	 * 根据名称获取ID生成器
	 *
	 * @param name Bean名称，如：redisIdGenerator, uuidIdGenerator
	 * @return ID生成器
	 * @throws IllegalArgumentException 如果生成器不存在
	 */
	public IdGenerator getGenerator(String name) {
		IdGenerator generator = generatorMap.get(name);
		if (generator == null) {
			throw new IllegalArgumentException("ID生成器不存在: " + name + ", 可用生成器: " + generatorMap.keySet());
		}
		return generator;
	}

	/**
	 * 获取 Redis ID 生成器
	 *
	 * @return Redis ID 生成器
	 */
	public IdGenerator getRedisGenerator() {
		return getGenerator("redisIdGenerator");
	}

	/**
	 * 获取 UUID ID 生成器
	 *
	 * @return UUID ID 生成器
	 */
	public IdGenerator getUuidGenerator() {
		return getGenerator("uuidIdGenerator");
	}

	/**
	 * 获取所有可用的生成器名称
	 *
	 * @return 生成器名称集合（不可修改）
	 */
	public Set<String> getAvailableGenerators() {
		return Collections.unmodifiableSet(generatorMap.keySet());
	}
}
