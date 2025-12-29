package cn.xgt.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import cn.xgt.UniverseDemoApplication;

/**
 * @author XGT
 * @description 基础测试类
 * @date 2025/12/29
 */
@SpringBootTest(classes = UniverseDemoApplication.class)
public class UniverseTest {

	protected Logger logger = LoggerFactory.getLogger(UniverseTest.class);

	/**
	 * 每个测试方法执行前打印测试开始信息
	 */
	@BeforeEach
	public void printTestStartMessage(TestInfo testInfo) {
		logger.info("===========================开始执行测试方法{}.{}==========================",
				testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown"), testInfo.getDisplayName());
	}

	/**
	 * 每个测试方法执行后打印测试结束信息
	 */
	@AfterEach
	public void printTestEndMessage(TestInfo testInfo) {
		logger.info("===========================测试方法执行完成{}.{}==========================",
				testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown"), testInfo.getDisplayName());
	}
}
