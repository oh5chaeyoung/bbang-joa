package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.request.EmailValidCodeRequest;

public interface EmailService {
	boolean sendCode(EmailCheckRequest request);
	boolean checkValidCode(EmailValidCodeRequest request);
}
