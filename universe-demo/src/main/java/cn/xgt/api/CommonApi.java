package cn.xgt.api;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.xgt.entity.User;
import cn.xgt.mapper.UserMapper;
import cn.xgt.universe.common.util.BeanCopyUtils;
import cn.xgt.vo.CopyDTO;
import cn.xgt.vo.CopyEntity;
import cn.xgt.vo.CopyVO;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * @author XGT
 * @description 测试公共方法
 * @date 2025/12/26
 */
@RestController
@RequestMapping(value = "restful/common")
public class CommonApi {

    private Logger logger = LoggerFactory.getLogger(MaskApi.class);

    /**
     * 测试 convertTo - 基础转换（无回调）
     */
    @GetMapping(value = "testConvertToBasic")
    public Map<String, Object> testConvertToBasic() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            CopyEntity source = createUserEntity(1L, "张三", "zhangsan@example.com", 25,
                    new BigDecimal("1000.50"), new Date(), "北京市朝阳区", true, "13800138000");

            // 执行转换（UserDTO只包含部分字段）
            CopyDTO target = BeanCopyUtils.convertTo(source, CopyDTO::new);
            // 等价于 CopyDTO target = BeanCopyUtils.convertTo(source, ()->new CopyDTO());

            // 验证结果
            result.put("success", true);
            result.put("message", "基础转换测试成功 - UserDTO只包含部分字段");
            result.put("source", source);
            result.put("target", target);
            result.put("note", "UserDTO只包含id、username、email、age字段，其他字段不会被复制");
            logger.info("测试 convertTo 基础转换 - 成功: {}", JSONUtil.toJsonStr(target));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertTo 基础转换 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertTo - 带回调函数（处理特殊字段）
     */
    @GetMapping(value = "testConvertToWithCallback")
    public Map<String, Object> testConvertToWithCallback() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            CopyEntity source = createUserEntity(1L, "李四", "lisi@example.com", 30,
                    new BigDecimal("50000.00"), new Date(), "上海市浦东新区", true, "13900139000");

            // 执行转换（带回调，使用UserVO包含扩展字段）
            CopyVO target = BeanCopyUtils.convertTo(
                    source,
                    CopyVO::new,
                    (s, t) -> {
                        // 格式化金额显示
                        if (s.getBalance() != null) {
                            t.setBalanceStr("¥" + s.getBalance().setScale(4, java.math.RoundingMode.HALF_UP));
                            // 根据金额设置等级
                            if (s.getBalance().compareTo(new BigDecimal("10000")) > 0) {
                                t.setUserLevel("VIP客户");
                            } else if (s.getBalance().compareTo(new BigDecimal("5000")) > 0) {
                                t.setUserLevel("高级客户");
                            } else {
                                t.setUserLevel("普通客户");
                            }
                        }
                        // 格式化时间显示
                        if (s.getCreateTime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            t.setCreateTimeStr(sdf.format(s.getCreateTime()));
                        }
                        // 组合显示信息
                        t.setDisplayInfo(s.getUsername() + " - " + s.getMobile());
                        // 设置状态描述
                        t.setStatusDesc(s.getActive() ? "活跃用户" : "非活跃用户");
                    }
            );

            result.put("success", true);
            result.put("message", "带回调转换测试成功 - UserVO包含扩展字段");
            result.put("source", source);
            result.put("target", target);
            logger.info("测试 convertTo 带回调 - 成功: {}", JSONUtil.toJsonStr(target));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertTo 带回调 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertTo - source 为 null 时抛出异常
     */
    @GetMapping(value = "testConvertToSourceNull")
    public Map<String, Object> testConvertToSourceNull() {
        Map<String, Object> result = new HashMap<>();
        try {
            BeanCopyUtils.convertTo(null, CopyDTO::new);
            result.put("success", false);
            result.put("message", "应该抛出异常，但没有抛出");
        } catch (IllegalArgumentException e) {
            result.put("success", true);
            result.put("message", "正确抛出异常: " + e.getMessage());
            result.put("exception", e.getClass().getName());
            logger.info("测试 convertTo source为null - 正确抛出异常: {}", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抛出异常类型不正确: " + e.getClass().getName());
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 convertTo - targetSupplier 为 null 时抛出异常
     */
    @GetMapping(value = "testConvertToTargetSupplierNull")
    public Map<String, Object> testConvertToTargetSupplierNull() {
        Map<String, Object> result = new HashMap<>();
        try {
            CopyEntity source = createUserEntity(1L, "张三", "test@example.com", 25,
                    new BigDecimal("1000.00"), new Date(), "北京市", true, "13800138000");
            BeanCopyUtils.convertTo(source, null);
            result.put("success", false);
            result.put("message", "应该抛出异常，但没有抛出");
        } catch (IllegalArgumentException e) {
            result.put("success", true);
            result.put("message", "正确抛出异常: " + e.getMessage());
            result.put("exception", e.getClass().getName());
            logger.info("测试 convertTo targetSupplier为null - 正确抛出异常: {}", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抛出异常类型不正确: " + e.getClass().getName());
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 convertListTo - 基础列表转换（默认跳过null）
     */
    @GetMapping(value = "testConvertListToBasic")
    public Map<String, Object> testConvertListToBasic() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("1000.50"), new Date(), "北京市", true, "13800138000"),
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("2000.00"), new Date(), "上海市", false, "13900139000"),
                    createUserEntity(3L, "王五", "wangwu@example.com", 28, new BigDecimal("1500.75"), new Date(), "广州市", true, "13700137000")
            );

            // 执行转换
            List<CopyDTO> dtos = BeanCopyUtils.convertListTo(users, CopyDTO::new);

            result.put("success", true);
            result.put("message", "基础列表转换测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", dtos.size());
            result.put("sources", users);
            result.put("targets", dtos);
            logger.info("测试 convertListTo 基础转换 - 成功: 源{}个, 目标{}个", users.size(), dtos.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 基础转换 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - 跳过null值（默认行为）
     */
    @GetMapping(value = "testConvertListToSkipNull")
    public Map<String, Object> testConvertListToSkipNull() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据（包含null值）
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("1000.50"), new Date(), "北京市", true, "13800138000"),
                    null,  // null 值
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("2000.00"), new Date(), "上海市", false, "13900139000"),
                    null,  // null 值
                    createUserEntity(3L, "王五", "wangwu@example.com", 28, new BigDecimal("1500.75"), new Date(), "广州市", true, "13700137000")
            );

            // 执行转换（默认跳过null）
            List<CopyDTO> dtos = BeanCopyUtils.convertListTo(users, CopyDTO::new);

            result.put("success", true);
            result.put("message", "跳过null值测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", dtos.size());
            result.put("sourceNullCount", users.stream().filter(Objects::isNull).count());
            result.put("targetNullCount", dtos.stream().filter(Objects::isNull).count());
            result.put("sources", users);
            result.put("targets", dtos);
            logger.info("测试 convertListTo 跳过null - 成功: 源{}个(含{}个null), 目标{}个(含{}个null)",
                    users.size(), users.stream().filter(Objects::isNull).count(),
                    dtos.size(), dtos.stream().filter(Objects::isNull).count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 跳过null - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - 保留null值（skipNull=false）
     */
    @GetMapping(value = "testConvertListToKeepNull")
    public Map<String, Object> testConvertListToKeepNull() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据（包含null值）
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("1000.50"), new Date(), "北京市", true, "13800138000"),
                    null,  // null 值
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("2000.00"), new Date(), "上海市", false, "13900139000"),
                    null,  // null 值
                    createUserEntity(3L, "王五", "wangwu@example.com", 28, new BigDecimal("1500.75"), new Date(), "广州市", true, "13700137000")
            );

            // 执行转换（保留null）
            List<CopyDTO> dtos = BeanCopyUtils.convertListTo(users, CopyDTO::new, false);

            result.put("success", true);
            result.put("message", "保留null值测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", dtos.size());
            result.put("sourceNullCount", users.stream().filter(Objects::isNull).count());
            result.put("targetNullCount", dtos.stream().filter(Objects::isNull).count());
            result.put("sources", users);
            result.put("targets", dtos);
            logger.info("测试 convertListTo 保留null - 成功: 源{}个(含{}个null), 目标{}个(含{}个null)",
                    users.size(), users.stream().filter(Objects::isNull).count(),
                    dtos.size(), dtos.stream().filter(Objects::isNull).count());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 保留null - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - 带回调函数
     */
    @GetMapping(value = "testConvertListToWithCallback")
    public Map<String, Object> testConvertListToWithCallback() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("15000.00"), new Date(), "北京市", true, "13800138000"),
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("8000.00"), new Date(), "上海市", false, "13900139000"),
                    createUserEntity(3L, "王五", "wangwu@example.com", 28, new BigDecimal("3000.00"), new Date(), "广州市", true, "13700137000")
            );

            // 执行转换（带回调，使用UserVO）
            List<CopyVO> userVOs = BeanCopyUtils.convertListTo(
                    users,
                    CopyVO::new,
                    (source, target) -> {
                        // 格式化金额显示
                        if (source.getBalance() != null) {
                            target.setBalanceStr("¥" + source.getBalance().setScale(4, java.math.RoundingMode.HALF_UP));
                            // 根据金额设置等级
                            if (source.getBalance().compareTo(new BigDecimal("10000")) > 0) {
                                target.setUserLevel("VIP客户");
                            } else if (source.getBalance().compareTo(new BigDecimal("5000")) > 0) {
                                target.setUserLevel("高级客户");
                            } else {
                                target.setUserLevel("普通客户");
                            }
                        }
                        // 格式化时间显示
                        if (source.getCreateTime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            target.setCreateTimeStr(sdf.format(source.getCreateTime()));
                        }
                        // 组合显示信息
                        target.setDisplayInfo(source.getUsername() + " - " + source.getMobile());
                        // 设置状态描述
                        target.setStatusDesc(source.getActive() ? "活跃用户" : "非活跃用户");
                    },
                    true  // 跳过null
            );

            result.put("success", true);
            result.put("message", "带回调列表转换测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", userVOs.size());
            result.put("sources", users);
            result.put("targets", userVOs);
            logger.info("测试 convertListTo 带回调 - 成功: {}", JSONUtil.toJsonStr(userVOs));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 带回调 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - 复杂场景：混合null值、回调、保留null
     */
    @GetMapping(value = "testConvertListToComplex")
    public Map<String, Object> testConvertListToComplex() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("15000.00"), new Date(), "北京市", true, "13800138000"),
                    null,
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("8000.00"), new Date(), "上海市", false, "13900139000"),
                    null,
                    createUserEntity(3L, "王五", "wangwu@example.com", 28, new BigDecimal("3000.00"), new Date(), "广州市", true, "13700137000")
            );

            // 执行转换（带回调，保留null，使用UserVO）
            List<CopyVO> userVOs = BeanCopyUtils.convertListTo(
                    users,
                    CopyVO::new,
                    (source, target) -> {
                        // 格式化金额显示
                        if (source.getBalance() != null) {
                            target.setBalanceStr("¥" + source.getBalance().setScale(4, java.math.RoundingMode.HALF_UP));
                            // 根据金额设置等级
                            if (source.getBalance().compareTo(new BigDecimal("10000")) > 0) {
                                target.setUserLevel("VIP客户");
                            } else if (source.getBalance().compareTo(new BigDecimal("5000")) > 0) {
                                target.setUserLevel("高级客户");
                            } else {
                                target.setUserLevel("普通客户");
                            }
                        }
                        // 格式化时间显示
                        if (source.getCreateTime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            target.setCreateTimeStr(sdf.format(source.getCreateTime()));
                        }
                        // 组合显示信息
                        target.setDisplayInfo(source.getUsername() + " - " + source.getMobile());
                        // 设置状态描述
                        target.setStatusDesc(source.getActive() ? "活跃用户" : "非活跃用户");
                    },
                    false  // 保留null
            );

            result.put("success", true);
            result.put("message", "复杂场景测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", userVOs.size());
            result.put("sources", users);
            result.put("targets", userVOs);
            logger.info("测试 convertListTo 复杂场景 - 成功: 源{}个, 目标{}个", users.size(), userVOs.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 复杂场景 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - sources 为 null 时抛出异常
     */
    @GetMapping(value = "testConvertListToSourcesNull")
    public Map<String, Object> testConvertListToSourcesNull() {
        Map<String, Object> result = new HashMap<>();
        try {
            BeanCopyUtils.convertListTo(null, CopyDTO::new);
            result.put("success", false);
            result.put("message", "应该抛出异常，但没有抛出");
        } catch (IllegalArgumentException e) {
            result.put("success", true);
            result.put("message", "正确抛出异常: " + e.getMessage());
            result.put("exception", e.getClass().getName());
            logger.info("测试 convertListTo sources为null - 正确抛出异常: {}", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抛出异常类型不正确: " + e.getClass().getName());
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 convertListTo - sources 为 null 时抛出异常
     */
    @GetMapping(value = "testConvertListToSourcesEmpty")
    public Map<String, Object> testConvertListToSourcesEmpty() {
        Map<String, Object> result = new HashMap<>();
        try {
            BeanCopyUtils.convertListTo(Collections.EMPTY_LIST, CopyDTO::new);
            result.put("success", false);
            result.put("message", "应该抛出异常，但没有抛出");
        } catch (IllegalArgumentException e) {
            result.put("success", true);
            result.put("message", "正确抛出异常: " + e.getMessage());
            result.put("exception", e.getClass().getName());
            logger.info("测试 convertListTo sources为null - 正确抛出异常: {}", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抛出异常类型不正确: " + e.getClass().getName());
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 convertListTo - 部分字段转换（UserDTO只包含部分字段）
     */
    @GetMapping(value = "testConvertListToPartialFields")
    public Map<String, Object> testConvertListToPartialFields() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("1000.50"), new Date(), "北京市", true, "13800138000"),
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("2000.00"), new Date(), "上海市", false, "13900139000")
            );

            // 执行转换（UserDTO只包含部分字段）
            List<CopyDTO> dtos = BeanCopyUtils.convertListTo(users, CopyDTO::new);

            result.put("success", true);
            result.put("message", "部分字段转换测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", dtos.size());
            result.put("note", "UserDTO只包含id、username、email、age字段，其他字段（balance、createTime、address、active、mobile）不会被复制");
            result.put("sources", users);
            result.put("targets", dtos);
            logger.info("测试 convertListTo 部分字段 - 成功: 源{}个, 目标{}个", users.size(), dtos.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 部分字段 - 失败", e);
        }
        return result;
    }

    /**
     * 测试 convertListTo - 验证每次调用get()都创建新对象
     */
    @GetMapping(value = "testConvertListToNewObjectPerCall")
    public Map<String, Object> testConvertListToNewObjectPerCall() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 准备数据
            List<CopyEntity> users = Arrays.asList(
                    createUserEntity(1L, "张三", "zhangsan@example.com", 25, new BigDecimal("1000.50"), new Date(), "北京市", true, "13800138000"),
                    createUserEntity(2L, "李四", "lisi@example.com", 30, new BigDecimal("2000.00"), new Date(), "上海市", false, "13900139000")
            );

            // 执行转换
            List<CopyDTO> dtos = BeanCopyUtils.convertListTo(users, CopyDTO::new);

            // 验证结果：每个元素都是不同的对象
            boolean allDifferent = dtos.get(0) != dtos.get(1);
            boolean contentDifferent = !dtos.get(0).getUsername().equals(dtos.get(1).getUsername());

            result.put("success", true);
            result.put("message", "验证每次调用get()都创建新对象测试成功");
            result.put("sourceSize", users.size());
            result.put("targetSize", dtos.size());
            result.put("allDifferent", allDifferent);
            result.put("contentDifferent", contentDifferent);
            result.put("note", "每个元素都是不同的对象实例");
            result.put("sources", users);
            result.put("targets", dtos);
            logger.info("测试 convertListTo 新对象验证 - 成功: 所有对象都不同: {}", allDifferent);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
            logger.error("测试 convertListTo 新对象验证 - 失败", e);
        }
        return result;
    }

    /**
     * 创建 UserEntity 测试对象
     */
    private CopyEntity createUserEntity(Long id, String username, String email, Integer age,
                                        BigDecimal balance, Date createTime, String address,
                                        Boolean active, String mobile) {
        CopyEntity user = new CopyEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setAge(age);
        user.setBalance(balance);
        user.setCreateTime(createTime);
        user.setAddress(address);
        user.setActive(active);
        user.setMobile(mobile);
        return user;
    }


    @Resource
    private UserMapper userMapper;
    @PostMapping(value = "testTiDb")
    public User testTiDb(@RequestBody User user) {
        /*User user = new User();
        user.setName("universe001");
        user.setEmail("1576745517@qq.com");*/
        userMapper.insert(user);

        logger.info("user: {}", JSONUtil.toJsonStr(user));

        return userMapper.findById(user.getId());
    }

    @Resource
    private RedissonClient redissonClient;
    @PostMapping("testRedisson")
    public Map<String, Object> testRedisson(@RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {
        String lockKey = "lock:test:redisson:simple";

        logger.info("testRedisson start, threadCount={}, lockKey={}", threadCount, lockKey);

        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicInteger success = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger fail = new java.util.concurrent.atomic.AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            pool.submit(() -> {
                String threadName = "T-" + idx;
                RLock lock = redissonClient.getLock(lockKey);
                try {
                    logger.info("[{}] tryLock begin", threadName);

                    // 不等待，抢不到立即返回
                    boolean ok = lock.tryLock(2, 3, TimeUnit.SECONDS);
                    if (ok) {
                        try {
                            int c = success.incrementAndGet();
                            logger.info("[{}] lock success, successCount={}", threadName, c);

                            Thread.sleep(200); // 模拟业务
                            logger.info("[{}] business done", threadName);
                        } finally {
                            if (lock.isHeldByCurrentThread()) {
                                lock.unlock();
                                logger.info("[{}] unlock success", threadName);
                            } else {
                                logger.warn("[{}] not lock owner, skip unlock", threadName);
                            }
                        }
                    } else {
                        int c = fail.incrementAndGet();
                        logger.info("[{}] lock fail, failCount={}", threadName, c);
                    }
                } catch (Exception e) {
                    int c = fail.incrementAndGet();
                    logger.error("[{}] exception, failCount={}", threadName, c, e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();

        Map<String, Object> result = new HashMap<>();
        result.put("threadCount", threadCount);
        result.put("success", success.get());
        result.put("fail", fail.get());
        result.put("lockKey", lockKey);

        logger.info("testRedisson end, result={}", JSONUtil.toJsonStr(result));
        return result;
    }
}
