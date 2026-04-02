package cn.xgt.universe.common.config;

/**
 * @author XGT
 * @description TODO
 * @date 2026/4/1
 */
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "universe.redisson")
public class UniverseRedissonProperties {

	/**
	 * 是否启用分布式锁组件
	 */
	private boolean enabled = true;

	/**
	 * 连接池最小空闲数（可选覆盖）
	 */
	private Integer connectionMinimumIdleSize;

	/**
	 * 连接池大小（可选覆盖）
	 */
	private Integer connectionPoolSize;

	/**
	 * 看门狗超时时间（毫秒），用于未显式 leaseTime 的锁自动续期机制
	 */
	private Long lockWatchdogTimeoutMs;
}
