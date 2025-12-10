package cn.xgt.universe.idgenerator;

/**
 * @author XGT
 * @description TODOID生成器自动配置类
 * @date 2025/11/4
 */
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.xgt.universe.common.util.RedisRepository;

@Configuration
@ConditionalOnClass({StringRedisTemplate.class})
@ConditionalOnProperty(prefix = "universe.id-generator", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorAutoConfiguration {

	/**
	 * 创建 RedisRepository Bean
	 * 如果项目中没有自定义的 IdRedisRepository，则创建默认的
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisRepository redisRepository(StringRedisTemplate stringRedisTemplate) {
		return new RedisRepository(stringRedisTemplate);
	}

	/**
	 * 创建 IdGenerator Bean
	 * 通过方法参数注入依赖，确保依赖正确注入
	 */
	@Bean("redisIdGenerator")
	@ConditionalOnMissingBean(name = "redisIdGenerator")
	public IdGenerator idGenerator(RedisRepository redisRepository, IdGeneratorProperties properties) {
		return new RedisIdGenerator(redisRepository, properties);
	}

	@Bean("uuidIdGenerator")
	@ConditionalOnMissingBean(name = "uuidIdGenerator")
	public IdGenerator uuidIdGenerator() {
		return new UuidIdGenerator();
	}

	/**
	 * 创建 IdGeneratorFactory，方便获取不同的生成器
	 */
	@Bean
	@ConditionalOnMissingBean
	public IdGeneratorFactory idGeneratorFactory() {
		return new IdGeneratorFactory();
	}
}
