package cn.xgt.universe.idgenerator;

/**
 * @author XGT
 * @description 基于Redis的分布式ID生成器实现
 * @date 2025/11/4
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.xgt.universe.common.util.RedisRepository;


//@Component
public class RedisIdGenerator implements IdGenerator {

	private static final Logger logger = LoggerFactory.getLogger(RedisIdGenerator.class);

	//@Autowired
	private RedisRepository redisRepository;

	//@Autowired
	private IdGeneratorProperties properties;

	// 添加构造函数用于依赖注入
	public RedisIdGenerator(RedisRepository redisRepository, IdGeneratorProperties properties) {
		if (redisRepository == null) {
			throw new IllegalArgumentException("RedisRepository cannot be null");
		}
		if (properties == null) {
			throw new IllegalArgumentException("IdGeneratorProperties cannot be null");
		}

		this.redisRepository = redisRepository;
		this.properties = properties;
	}

	/**
	 * 生成分布式ID(基于Redis)
	 * @param key redis key
	 * @return ID
	 */
	@Override
	public Long nextId(String key) {
		if (key == null || key.trim().isEmpty()) {
			key = properties.getDefaultKey();
		}

		String redisKey = properties.getKeyPrefix() + ":" + key;
		Long id = redisRepository.increment(redisKey, 1);

		// 设置过期时间（可选）
		if (properties.getExpireTime() > 0) {
			redisRepository.expire(redisKey, properties.getExpireTime());
		}

		logger.debug("生成ID: key={}, redisKey={}, id={}", key, redisKey, id);
		return id;
	}

	/**
	 * 生成分布式ID(基于Redis)
	 * 无参时,redis key取[properties.getKeyPrefix() : IdGeneratorProperties.defaultKey]的值.默认[id_generator:id_generator_default]
	 * @return ID
	 */
	@Override
	public Long nextId() {
		return nextId(properties.getDefaultKey());
	}

	@Override
	public String nextIdString(String key) {
		return nextId(key).toString();
	}

	@Override
	public String nextIdString() {
		return nextIdString(null);
	}

	/**
	 * 生成分布式ID(基于Redis)
	 * @param key redis key
	 * @param prefix 结果前缀拼接
	 * @return ID
	 */
	@Override
	public String nextIdWithPrefix(String key, String prefix) {
		Long id = nextId(key);
		return prefix + id;
	}

	/**
	 * 生成分布式ID(基于Redis)
	 * @param prefix 结果前缀拼接
	 * @return ID
	 */
	@Override
	public String nextIdWithPrefix(String prefix) {
		return nextIdWithPrefix(properties.getDefaultKey(), prefix);
	}

	/**
	 * 生成分布式ID(基于Redis)
	 * @param key redis key
	 * @param prefix 结果前缀拼接
	 * @param length 序列号长度
	 * @return ID
	 */
	@Override
	public String nextIdWithPrefixAndLength(String key, String prefix, int length) {
		Long id = nextId(key);
		String formattedId = String.format("%0" + length + "d", id);
		return prefix + formattedId;
	}
}
