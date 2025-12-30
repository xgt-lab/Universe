

```markdown
<div align="center">

# 🌌 Universe

**一个强大的 Java 工具库集合，为 Spring Boot 应用提供开箱即用的实用工具**

[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/xgt-open/Universe.svg)](https://jitpack.io/#xgt-open/Universe)

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [使用文档](#-使用文档) • [示例代码](#-示例代码) • [更新日志](#-更新日志)

</div>

---

## 📖 项目简介

**Universe** 是一个精心设计的 Java 工具库集合，专为 Spring Boot 应用打造。它提供了分布式ID生成、数据脱敏等常用功能，帮助开发者快速构建高质量的企业级应用。

### 🎯 设计理念

- **零配置**：开箱即用，自动配置
- **高性能**：基于 Redis 的分布式ID生成，支持高并发场景
- **易扩展**：插件化设计，轻松扩展新功能
- **安全可靠**：完善的数据脱敏策略，保护敏感信息

---

## ✨ 功能特性

### 🆔 分布式ID生成器 (`universe-id-generator`)

- ✅ **多种生成策略**：支持 Redis 自增ID、UUID、雪花算法等
- ✅ **高性能**：基于 Redis INCR 命令，支持高并发
- ✅ **灵活配置**：可自定义前缀、序列号长度、过期时间
- ✅ **工厂模式**：统一的 `IdGeneratorFactory` 管理所有生成器
- ✅ **自动装配**：Spring Boot 自动配置，零代码接入

### 🔒 数据脱敏工具 (`universe-mask`)

- ✅ **多种脱敏策略**：姓名、手机号、身份证、银行卡、邮箱、地址、金额等
- ✅ **注解驱动**：通过 `@Mask` 注解轻松实现字段脱敏
- ✅ **自定义规则**：支持自定义脱敏前后缀长度和替换符号
- ✅ **Jackson 集成**：与 Spring Boot 的 JSON 序列化无缝集成
- ✅ **类型安全**：基于枚举的脱敏类型，编译时检查

### 🛠️ 公共工具 (`universe-common`)

- ✅ **Redis 工具**：封装的 Redis 操作工具类
- ✅ **通用工具**：常用工具类和辅助方法

---

## 🚀 快速开始

### Maven 依赖

#### 方式一：使用 JitPack（推荐）

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- ID 生成器 -->
    <dependency>
        <groupId>com.github.xgt-open.Universe</groupId>
        <artifactId>universe-id-generator</artifactId>
        <version>1.0.8</version>
    </dependency>
    
    <!-- 数据脱敏工具 -->
    <dependency>
        <groupId>com.github.xgt-open.Universe</groupId>
        <artifactId>universe-mask</artifactId>
        <version>1.0.8</version>
    </dependency>
</dependencies>
```

#### 方式二：本地安装

```bash
git clone https://github.com/xgt-open/Universe.git
cd Universe
mvn clean install
```

然后在你的项目中引入：

```xml
<dependencies>
    <dependency>
        <groupId>cn.xgt</groupId>
        <artifactId>universe-id-generator</artifactId>
        <version>1.0.8</version>
    </dependency>
    
    <dependency>
        <groupId>cn.xgt</groupId>
        <artifactId>universe-mask</artifactId>
        <version>1.0.8</version>
    </dependency>
</dependencies>
```

### 前置要求

- ✅ JDK 8 或更高版本
- ✅ Spring Boot 2.7.x
- ✅ Redis（如果使用 Redis ID 生成器）

---

## 📚 使用文档

### 🆔 ID 生成器使用指南

#### 1. 配置文件（可选）

```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0

# ID 生成器配置（可选）
universe:
  id-generator:
    key-prefix: id_generator          # Redis Key 前缀，默认: id_generator
    default-key: id_generator_default # 默认业务键，默认: id_generator_default
    expire-time: 0                    # Key 过期时间（秒），0 表示不过期
```

#### 2. 代码示例

**方式一：通过工厂类获取（推荐）**

```java
@RestController
@RequestMapping("/api")
public class OrderController {
    
    @Autowired
    private IdGeneratorFactory idGeneratorFactory;
    
    @GetMapping("/order-id")
    public String generateOrderId() {
        // 获取 Redis ID 生成器
        IdGenerator redisGenerator = idGeneratorFactory.getRedisGenerator();
        
        // 生成订单ID（带前缀）
        String orderId = redisGenerator.nextIdWithPrefix("ORDER_");
        return orderId; // 输出: ORDER_10001
    }
    
    @GetMapping("/uuid")
    public String generateUuid() {
        // 获取 UUID 生成器
        IdGenerator uuidGenerator = idGeneratorFactory.getUuidGenerator();
        
        // 生成 UUID
        String uuid = uuidGenerator.nextIdString();
        return uuid; // 输出: 550e8400-e29b-41d4-a716-446655440000
    }
}
```

**方式二：直接注入生成器**

```java
@RestController
public class UserController {
    
    // 注入 Redis ID 生成器
    @Autowired
    @Qualifier("redisIdGenerator")
    private IdGenerator redisIdGenerator;
    
    // 注入 UUID 生成器
    @Autowired
    @Qualifier("uuidIdGenerator")
    private IdGenerator uuidIdGenerator;
    
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        // 生成用户ID
        Long userId = redisIdGenerator.nextId("user");
        user.setId(userId);
        return userService.save(user);
    }
}
```

#### 3. ID 生成器方法说明

```java
// 生成 Long 类型 ID
Long id = generator.nextId("order");              // 使用指定业务键
Long id = generator.nextId();                     // 使用默认业务键

// 生成 String 类型 ID
String idStr = generator.nextIdString("order");
String idStr = generator.nextIdString();

// 生成带前缀的 ID
String orderId = generator.nextIdWithPrefix("order", "ORDER_");
// 输出: ORDER_10001

// 生成带前缀和固定长度的 ID
String orderId = generator.nextIdWithPrefixAndLength("order", "ORDER_", 8);
// 输出: ORDER_00010001 (数字部分固定 8 位，不足补 0)
```

---

### 🔒 数据脱敏工具使用指南

#### 1. 在实体类字段上添加 `@Mask` 注解

```java
public class UserVO {
    private Long id;
    
    @Mask(category = CATEGORT.NAME)  // 姓名脱敏：张**三
    private String name;
    
    @Mask(category = CATEGORT.MOBILE)  // 手机号脱敏：138****8888
    private String mobile;
    
    @Mask(category = CATEGORT.ID_NUM)  // 身份证脱敏：110***********1234
    private String idCard;
    
    @Mask(category = CATEGORT.CARD_NUM)  // 银行卡脱敏：6222****1234
    private String bankCard;
    
    @Mask(category = CATEGORT.EMAIL)  // 邮箱脱敏：abc***@example.com
    private String email;
    
    @Mask(category = CATEGORT.ADDRESS)  // 地址脱敏：北京市海淀区***123号
    private String address;
    
    @Mask(category = CATEGORT.MONEY)  // 金额脱敏：***.00
    private BigDecimal amount;
    
    // 自定义脱敏规则
    @Mask(
        category = CATEGORT.CUSTOM,
        prefixNoMaskLen = 3,      // 前 3 位不脱敏
        suffixNoMaskLen = 2,      // 后 2 位不脱敏
        asterisk = "*"            // 使用 * 号替换
    )
    private String customField;  // 示例: abc***12
}
```

#### 2. 脱敏策略说明

| 类型 | 说明 | 示例 |
|------|------|------|
| `NAME` | 姓名脱敏 | 张三 → 张** |
| `MOBILE` | 手机号脱敏 | 13812345678 → 138****5678 |
| `ID_NUM` | 身份证脱敏 | 110101199001011234 → 110***********1234 |
| `CARD_NUM` | 银行卡脱敏 | 6222021234567890123 → 6222****9012 |
| `EMAIL` | 邮箱脱敏 | abc@example.com → abc***@example.com |
| `ADDRESS` | 地址脱敏 | 北京市海淀区中关村大街123号 → 北京市海淀区***123号 |
| `MONEY` | 金额脱敏 | 1234.56 → ***.56 |
| `CUSTOM` | 自定义脱敏 | 通过 `prefixNoMaskLen`、`suffixNoMaskLen` 自定义 |

#### 3. 自定义脱敏规则

```java
public class ProductVO {
    // 自定义：前 2 位保留，后 4 位保留，中间用 * 替换
    @Mask(
        category = CATEGORT.CUSTOM,
        prefixNoMaskLen = 2,
        suffixNoMaskLen = 4,
        asterisk = "*"
    )
    private String productCode;  // "ABCD12345678" → "AB****5678"
    
    // 自定义：前 1 位保留，其余全部脱敏
    @Mask(
        category = CATEGORT.CUSTOM,
        prefixNoMaskLen = 1,
        suffixNoMaskLen = 0,
        asterisk = "#"
    )
    private String secretKey;  // "SECRET123" → "S########"
}
```

#### 4. Controller 使用示例

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public UserVO getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        // 自动转换为 VO，敏感字段会自动脱敏
        UserVO vo = BeanUtils.copyProperties(user, UserVO.class);
        return vo;  // 返回的 JSON 中敏感字段已自动脱敏
    }
}
```

返回的 JSON 示例：

```json
{
    "id": 1,
    "name": "张**",
    "mobile": "138****5678",
    "idCard": "110***********1234",
    "bankCard": "6222****9012",
    "email": "abc***@example.com"
}
```

---

## 📦 项目模块

| 模块 | 说明 | 版本 |
|------|------|------|
| `universe-common` | 公共工具模块，包含 Redis 工具等 | 1.0.8 |
| `universe-id-generator` | 分布式ID生成器 | 1.0.8 |
| `universe-mask` | 数据脱敏工具 | 1.0.8 |
| `universe-demo` | 使用示例和演示代码 | - |

---

## 🎨 架构设计

```
Universe
├── universe-common          # 公共模块
│   └── Redis 工具类
├── universe-id-generator    # ID 生成器模块
│   ├── IdGenerator          # ID 生成器接口
│   ├── RedisIdGenerator     # Redis 实现
│   ├── UuidIdGenerator      # UUID 实现
│   ├── IdGeneratorFactory   # 工厂类
│   └── IdGeneratorAutoConfiguration  # 自动配置
└── universe-mask            # 数据脱敏模块
    ├── @Mask                # 脱敏注解
    ├── MaskSerializer       # Jackson 序列化器
    └── CATEGORT             # 脱敏类型枚举
```

---

## 🔧 配置说明

### ID 生成器配置

```yaml
universe:
  id-generator:
    # Redis Key 前缀，用于区分不同应用的 ID 生成器
    key-prefix: id_generator
    
    # 默认业务键，当调用 nextId() 不传 key 时使用
    default-key: id_generator_default
    
    # Key 过期时间（秒），0 表示永不过期
    # 建议根据业务需求设置，避免 Redis 中积累过多无用 Key
    expire-time: 0
```

### Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码，填写密码
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

---

## 💡 最佳实践

### ID 生成器

1. **业务键命名规范**：建议使用 `模块_业务` 的格式，如 `order_main`、`user_detail`
2. **前缀设计**：使用有意义的业务前缀，如 `ORDER_`、`USER_`，便于识别和排查
3. **序列号长度**：根据业务量级设置合适的长度，避免过早达到上限
4. **过期时间设置**：对于临时业务，设置合理的过期时间，避免 Redis 内存浪费

### 数据脱敏

1. **VO 设计**：建议创建独立的 VO 类用于返回数据，而不是直接使用实体类
2. **敏感字段识别**：全面识别需要脱敏的字段，包括姓名、手机号、身份证等
3. **自定义规则**：对于特殊的脱敏需求，使用 `CUSTOM` 类型灵活配置
4. **性能考虑**：脱敏操作在 JSON 序列化时进行，对性能影响极小

---

## ❓ 常见问题

### Q1: 如何使用雪花算法生成 ID？

A: 目前支持 Redis 和 UUID 两种方式。雪花算法需要根据业务需求自行实现，未来版本可能会加入。

### Q2: 脱敏后的数据如何恢复？

A: 数据脱敏是单向操作，一旦脱敏无法恢复。建议在数据库层面保留原始数据，只在展示层进行脱敏。

### Q3: 如何在非 Spring Boot 项目中使用？

A: 目前主要针对 Spring Boot 应用设计。如需在传统 Spring 项目中使用，需要手动配置 Bean。

### Q4: Redis 连接失败怎么办？

A: 确保 Redis 服务正常运行，检查配置的 host、port、password 是否正确。如果不使用 Redis ID 生成器，可以只使用 UUID 生成器。

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 开发规范

- 代码风格遵循 Google Java Style Guide
- 提交信息使用中文或英文，清晰描述改动内容
- 新增功能需要添加单元测试
- 更新 README 文档

---

## 📝 更新日志

### v1.0.8 (最新)
- ✅ 优化依赖管理，修复 JitPack 构建问题
- ✅ 完善文档和示例代码
- ✅ 改进自动配置机制

### v1.0.7
- ✅ 修复 `universe-common` 缺少 Spring Boot 依赖的问题
- ✅ 添加父 POM 依赖管理

### v1.0.6
- 🎉 初始版本发布
- ✅ 支持 Redis 和 UUID ID 生成
- ✅ 支持多种数据脱敏策略

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

---

## 👤 作者

**XGT**

- GitHub: [@xgt-open](https://github.com/xgt-open)

---

## 🌟 Star History

如果这个项目对你有帮助，请给一个 ⭐️ Star！

---

<div align="center">

**Made with ❤️ by XGT**

[⬆ 返回顶部](#-universe)

</div>
```

以上内容可直接复制到 README.md。包含：
- 徽章和导航链接
- 功能介绍
- 快速开始指南
- 详细使用文档与代码示例
- 配置说明
- 最佳实践
- 常见问题
- 更新日志

如需调整，请告知。
