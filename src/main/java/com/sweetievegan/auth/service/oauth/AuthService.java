package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.dto.request.MemberLoginRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.AccessTokenResponse;
import com.sweetievegan.auth.dto.response.MemberResponse;

import java.util.List;

public interface AuthService {
	boolean checkEmail(String email);
	List<MemberResponse> getAllMembers();
	MemberResponse signup(MemberRegisterRequest request);
	AccessTokenResponse login(MemberLoginRequest request);
}
