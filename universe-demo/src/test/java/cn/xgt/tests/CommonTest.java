package cn.xgt.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.xgt.universe.common.util.MailClient;
import cn.xgt.universe.idgenerator.IdGenerator;

/**
 * @author XGT
 * @description TODO
 * @date 2026/4/2
 */
public class CommonTest extends UniverseTest{

	@Autowired
	private MailClient mailClient;

	@Test
	public void testMailClient() {
		mailClient.sendText("1576745517@qq.com", "Test MailClient Subject", "Test MailClient Text");
	}
}
