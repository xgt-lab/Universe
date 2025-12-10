package cn.xgt.universe.common.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

//@Component
public class RedisRepository {

    Logger logger = LoggerFactory.getLogger(RedisRepository.class);
    //@Autowired
    private StringRedisTemplate template;

    // 添加构造函数用于注入 StringRedisTemplate
    public RedisRepository(StringRedisTemplate template) {
        this.template = template;
    }

    // 保留无参构造函数（向后兼容）
    public RedisRepository() {
    }

    public boolean set(String key, String value) {
        try {
            this.logger.info("redis写入内容, key:[ {} ], value:[ {} ].", key, value);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                this.template.opsForValue().set(key, value);
            }

            return true;
        } catch (Exception var4) {
            this.logger.error("redis写入失败", var4);
            return false;
        }
    }

    public boolean set(String key, String value, long expiryTime) {
        return this.set(key, value, expiryTime, TimeUnit.SECONDS);
    }

    public boolean set(String key, String value, long timeout, TimeUnit unit) {
        try {
            this.logger.info("redis写入内容, key:[ {} ], value:[ {} ], expiryTime:[ {} ], TimeUnit:[ {} ].", new Object[]{key, value, timeout, unit.toString()});
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                this.template.opsForValue().set(key, value, timeout, unit);
                return true;
            }
        } catch (Exception var7) {
            this.logger.error("redis写入失败", var7);
        }

        return false;
    }

    public String get(String key) {
        this.logger.info("获取redis数据, key:[ {} ].", key);
        if (StringUtils.isBlank(key)) {
            this.logger.info("获取redis数据失败, KEY不能为空.");
            throw new RuntimeException("获取redis数据失败, KEY不能为空.");
        } else {
            return (String)this.template.opsForValue().get(key);
        }
    }

    public boolean hasKey(String key) {
        this.logger.info("查询redis是否存在key:[ {} ].", key);
        if (StringUtils.isBlank(key)) {
            this.logger.info("查询redis是否存在key失败, key不能为空.");
            throw new RuntimeException("查询redis是否存在key失败, key不能为空.");
        } else {
            return this.template.hasKey(key);
        }
    }

    public Long increment(String key, int value) {
        this.logger.info("增量redis数据, key:[ {} ], value:[ {} ].", key, value);
        if (StringUtils.isBlank(key)) {
            this.logger.info("增量redis数据失败, KEY不能为空.");
            throw new RuntimeException("增量redis数据失败, KEY不能为空.");
        } else {
            return this.template.opsForValue().increment(key, (long)value);
        }
    }

    public boolean addSet(String key, Set<String> set) {
        try {
            this.logger.info("写入redis set数据, key:[ {} ].", key);
            if (StringUtils.isBlank(key)) {
                this.logger.info("写入redis set数据失败, KEY不能为空.");
            } else {
                if (!CollectionUtils.isEmpty(set)) {
                    this.template.opsForSet().add(key, set.toArray(new String[set.size()]));
                    return true;
                }

                this.logger.info("写入redis set数据失败, set不能为空.");
            }
        } catch (Exception var4) {
            this.logger.error("写入redis set数据失败", var4);
        }

        return false;
    }

    public boolean updateSet(String key, Set<String> set) {
        try {
            this.deleteKey(key);
            this.logger.info("写入redis set数据, key:[ {} ].", key);
            if (StringUtils.isBlank(key)) {
                this.logger.info("写入redis set数据失败, KEY不能为空.");
            } else {
                if (!CollectionUtils.isEmpty(set)) {
                    this.template.opsForSet().add(key, set.toArray(new String[set.size()]));
                    return true;
                }

                this.logger.info("写入redis set数据失败, set不能为空.");
            }
        } catch (Exception var4) {
            this.logger.error("写入redis set数据失败", var4);
        }

        return false;
    }

    public boolean setSet(String key, String... values) {
        try {
            this.logger.info("写入redis set数据, key:[ {} ], values:[ {} ].", key, values);
            if (StringUtils.isBlank(key)) {
                this.logger.info("写入redis set数据失败, KEY不能为空.");
            } else {
                if (values != null && values.length != 0) {
                    this.template.opsForSet().add(key, values);
                    return true;
                }

                this.logger.info("写入redis set数据失败, 数据不能为空.");
            }
        } catch (Exception var4) {
            this.logger.error("写入redis set数据失败", var4);
        }

        return false;
    }

    public Set<String> getSet(String key) {
        this.logger.info("获取redis set数据, key:[ {} ]", key);
        if (StringUtils.isBlank(key)) {
            this.logger.info("获取redis set数据失败, KEY不能为空.");
            throw new RuntimeException("获取redis set数据失败, KEY不能为空.");
        } else {
            return this.template.opsForSet().members(key);
        }
    }

    public Boolean isMember(String key, Object o) {
        this.logger.info("查看集合中是否存在指定数据, key:[ {} ], o:[ {} ].", key, o.toString());
        if (StringUtils.isBlank(key)) {
            this.logger.info("查看集合中是否存在指定数据失败, key不能为空.");
            throw new RuntimeException("查看集合中是否存在指定数据失败, key不能为空.");
        } else {
            return this.template.opsForSet().isMember(key, o);
        }
    }

    /*public <T> Set<T> keys(String keyPrefix) {
        this.logger.info("获取redis数据, keyPrefix:[ {} ].", keyPrefix);
        if (StringUtils.isBlank(keyPrefix)) {
            this.logger.info("获取redis数据失败, prefixKey不能为空.");
            throw new RuntimeException("获取redis数据失败, keyPrefix不能为空.");
        } else {
            return keyPrefix.endsWith("*") ? this.getMultipleKey(this.template.keys(keyPrefix)) : this.getMultipleKey(this.template.keys(keyPrefix + "*"));
        }
    }*/

    /*public <T> Set<T> getMultipleKey(Set<String> keySet) {
        this.logger.info("根据集合Key获取redis数据, keySet:[ {} ].", JSON.toJSONString(keySet));
        if (CollectionUtils.isEmpty(keySet)) {
            this.logger.info("根据集合Key获取redis数据, keySet为空.");
            throw new RuntimeException("获取redis数据失败, keySet为空.");
        } else {
            return this.template.opsForSet().union((Object)null, keySet);
        }
    }*/

    public Boolean expire(String key, long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        this.logger.info("设置redis数据过期时间, key:[ {} ], timeout:[ {} ], TimeUnit:[ {} ].", new Object[]{key, timeout, unit.toString()});
        if (StringUtils.isBlank(key)) {
            this.logger.info("设置redis数据过期时间失败, key不能为空.");
            throw new RuntimeException("设置redis数据过期时间失败, key不能为空.");
        } else {
            return this.template.expire(key, timeout, unit);
        }
    }

    public Long getExpiry(String key) {
        this.logger.info("获取redis数据过期时间, key:[ {} ].", key);
        if (StringUtils.isBlank(key)) {
            this.logger.info("获取redis数据过期时间失败, key不能为空.");
            throw new RuntimeException("获取redis数据过期时间失败, key不能为空.");
        } else {
            return this.template.getExpire(key);
        }
    }

    public void deleteKey(String key) {
        this.logger.info("删除redis数据, key:[ {} ].", key);
        if (StringUtils.isBlank(key)) {
            this.logger.info("删除redis数据失败, KEY不能为空.");
            throw new RuntimeException("删除redis数据失败, KEY不能为空.");
        } else {
            this.template.delete(key);
        }
    }

    public void deleteKeyPrefix(String keyPrefix) {
        this.logger.info("删除redis数据, keyPrefix:[ {} ].", keyPrefix);
        if (StringUtils.isBlank(keyPrefix)) {
            this.logger.info("删除redis数据失败, keyPrefix不能为空.");
            throw new RuntimeException("删除redis数据失败, keyPrefix不能为空.");
        } else {
            Set keySet;
            if (keyPrefix.endsWith("*")) {
                keySet = this.template.keys(keyPrefix);
            } else {
                keySet = this.template.keys(keyPrefix + "*");
            }

            this.template.delete(keySet);
        }
    }

    public boolean update(String key, String value) {
        this.logger.info("更新redis数据, key:[ {} ]", key);
        return this.hasKey(key) ? this.set(key, value, this.getExpiry(value), TimeUnit.SECONDS) : this.set(key, value);
    }
}
