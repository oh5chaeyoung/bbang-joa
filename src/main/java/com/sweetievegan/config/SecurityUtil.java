package com.sweetievegan.config;

import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

	private SecurityUtil() { }

	public static String getCurrentMemberId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_EXCEPTION);
		}

		return authentication.getName();
	}
}
