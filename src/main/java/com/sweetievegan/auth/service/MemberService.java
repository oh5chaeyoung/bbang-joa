package com.sweetievegan.auth.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.dto.response.MemberResponse;

public interface MemberService {
	MemberResponse changeMemberNickname(String email, String nickname);
	MemberResponse changeMemberPassword(String exPassword, String newPassword);
	Member getMemberDetail(Long id);
	String checkEmail(String email);
}
