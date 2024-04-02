package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.request.EmailValidCodeRequest;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import com.sweetievegan.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
	private final JavaMailSender javaMailSender;
	private final RedisUtil redisUtil;
	private static final Random random = new Random();

	@Value("${spring.mail.username}")
	private String from;

	@Override
	public boolean sendCode(EmailCheckRequest request) {
		Integer randomNumber = random.nextInt(888888) + 111111;
		String authKey = String.valueOf(randomNumber);
		sendAuthEmail(request.getEmail(), authKey);
		return true;
	}

	@Override
	public boolean checkValidCode(EmailValidCodeRequest request) {
		String codeFoundByEmail = redisUtil.getData(request.getEmail());
		if(codeFoundByEmail == null) {
			throw new GlobalException(GlobalErrorCode.EXPIRED_EMAIL_VALID_CODE);
		}
		return codeFoundByEmail.equals(request.getValidCode());
	}

	private void sendAuthEmail(String email, String authKey) {
		String subject = "[SweetieVegan] Please check your email code";
		String text = "\n\ncode : " + authKey;
		try{
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
			helper.setFrom(from);
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(text);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}

		/* Redis */
		try {
			redisUtil.setDataExpire(email, authKey, 60 * 3L);
		} catch (Exception e) {
			throw new GlobalException(GlobalErrorCode.FAIL_TO_SAVE_EMAIL_CODE);
		}
	}
}
