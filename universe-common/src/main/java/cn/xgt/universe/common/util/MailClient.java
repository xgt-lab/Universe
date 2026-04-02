package cn.xgt.universe.common.util;

/**
 * @author XGT
 * @description TODO
 * @date 2026/4/2
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class MailClient {

	@Resource
	private JavaMailSender javaMailSender;

	// spring.mail.username: 通常就是 resend 的账号
	@Value("${spring.mail.username:}")
	private String mailUsername;

	// 可选：spring.mail.from 作为默认发件人
	@Value("${spring.mail.from:}")
	private String mailFrom;

	private String resolveFrom() {
		if (StringUtils.isNotBlank(mailFrom)) {
			return mailFrom;
		}
		if (StringUtils.isNotBlank(mailUsername)) {
			return mailUsername;
		}
		return null;
	}

	public void sendText(String to, String subject, String text) {
		send(to, subject, text, false);
	}

	public void sendHtml(String to, String subject, String html) {
		send(to, subject, html, true);
	}

	private void send(String to, String subject, String content, boolean html) {
		try {
			if (StringUtils.isBlank(to)) throw new IllegalArgumentException("to 不能为空");
			if (StringUtils.isBlank(subject)) throw new IllegalArgumentException("subject 不能为空");
			if (content == null) throw new IllegalArgumentException("content 不能为空");

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(
					mimeMessage,
					false,
					StandardCharsets.UTF_8.name()
			);

			helper.setTo(to);
			helper.setSubject(subject);

			String from = resolveFrom();
			if (StringUtils.isNotBlank(from)) {
				helper.setFrom(from);
			}

			helper.setText(content, html);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			throw new RuntimeException("邮件发送失败, to=" + to + ", subject=" + subject, e);
		}
	}
}
