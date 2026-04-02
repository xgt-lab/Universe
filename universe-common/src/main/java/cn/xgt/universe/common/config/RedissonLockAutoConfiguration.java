package cn.xgt.universe.common.config;

/**
 * @author XGT
 * @description TODO
 * @date 2026/4/1
 */
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
@ConditionalOnClass({Redisson.class, RedisProperties.class})
@EnableConfigurationProperties(UniverseRedissonProperties.class)
@ConditionalOnProperty(prefix = "universe.redisson", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedissonLockAutoConfiguration {

	private static final int DEFAULT_TIMEOUT_MS = 10000;
	private static final int DEFAULT_MIN_IDLE = 1;
	private static final int DEFAULT_POOL_SIZE = 8;

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean(RedissonClient.class)
	public RedissonClient redissonClient(RedisProperties redisProperties,
										 UniverseRedissonProperties universeProps) {

		Config config = new Config();

		if (universeProps.getLockWatchdogTimeoutMs() != null && universeProps.getLockWatchdogTimeoutMs() > 0) {
			config.setLockWatchdogTimeout(universeProps.getLockWatchdogTimeoutMs());
		}

		/**
		 * 单节点 / 托管单入口（如 Upstash 常见形态）：useSingleServer()
		 * 主从 + Sentinel：useSentinelServers()
		 * Redis Cluster（分片集群）：useClusterServers()
		 * 云厂商提供多个主节点做复制（非 Sentinel/Cluster）：useReplicatedServers()
		 * 需要跨集群容灾/多活：useMultiClusterServers()（较高级场景）
		 * 快速判断
		 * 你拿到的是 一个地址，没 Sentinel 名称、没多个 node 地址列表 → 多半 SingleServer
		 * 你有 mymaster + sentinel nodes → Sentinel
		 * 你有多个 host:port 且是 Redis Cluster 拓扑 → Cluster
		 */
		SingleServerConfig server = config.useSingleServer()
				.setAddress(buildAddress(redisProperties))
				.setDatabase(redisProperties.getDatabase())
				.setTimeout(resolveTimeout(redisProperties.getTimeout()))
				.setConnectionMinimumIdleSize(
						universeProps.getConnectionMinimumIdleSize() != null
								? universeProps.getConnectionMinimumIdleSize() : DEFAULT_MIN_IDLE)
				.setConnectionPoolSize(
						universeProps.getConnectionPoolSize() != null
								? universeProps.getConnectionPoolSize() : DEFAULT_POOL_SIZE);

		// 用户名优先 spring.redis.username；否则尝试从 URL 提取
		String username = redisProperties.getUsername();
		if (StringUtils.isBlank(username) && StringUtils.isNotBlank(redisProperties.getUrl())) {
			username = extractUsernameFromUrl(redisProperties.getUrl());
		}
		if (StringUtils.isNotBlank(username)) {
			server.setUsername(username);
		}

		// 密码优先 spring.redis.password；否则尝试从 URL 提取
		String password = redisProperties.getPassword();
		if (StringUtils.isBlank(password) && StringUtils.isNotBlank(redisProperties.getUrl())) {
			password = extractPasswordFromUrl(redisProperties.getUrl());
		}
		if (StringUtils.isNotBlank(password)) {
			server.setPassword(password);
		}

		return Redisson.create(config);
	}

	private String buildAddress(RedisProperties redisProperties) {
		// 优先复用 spring.redis.url
		if (StringUtils.isNotBlank(redisProperties.getUrl())) {
			String url = redisProperties.getUrl().trim();
			if (url.startsWith("redis://") || url.startsWith("rediss://")) {
				return url;
			}
			// 用户误写 host:port 时兜底
			return "rediss://" + url;
		}

		// 兼容 host/port
		String host = redisProperties.getHost();
		Integer port = redisProperties.getPort();
		if (StringUtils.isBlank(host) || port == null) {
			throw new IllegalArgumentException("spring.redis.url 或 spring.redis.host/port 必须配置");
		}
		// Upstash 默认 TLS，优先 rediss
		return "rediss://" + host + ":" + port;
	}

	private int resolveTimeout(Duration timeout) {
		return timeout == null ? DEFAULT_TIMEOUT_MS : (int) timeout.toMillis();
	}

	private String extractUsernameFromUrl(String redisUrl) {
		try {
			URI uri = URI.create(redisUrl);
			String userInfo = uri.getUserInfo(); // username:password
			if (StringUtils.isBlank(userInfo) || !userInfo.contains(":")) {
				return null;
			}
			return userInfo.substring(0, userInfo.indexOf(':'));
		} catch (Exception ignored) {
			return null;
		}
	}

	private String extractPasswordFromUrl(String redisUrl) {
		try {
			URI uri = URI.create(redisUrl);
			String userInfo = uri.getUserInfo(); // username:password
			if (StringUtils.isBlank(userInfo) || !userInfo.contains(":")) {
				return null;
			}
			return userInfo.substring(userInfo.indexOf(':') + 1);
		} catch (Exception ignored) {
			return null;
		}
	}
}
