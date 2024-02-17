package com.sweetievegan.auth.dto.request;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.util.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterRequest {
	private String email;
	private String password;
	private String nickname;
	private String firstname;
	private String lastname;

	public Member toMember(PasswordEncoder passwordEncoder) {
		return Member.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.nickname(nickname)
				.firstname(firstname)
				.lastname(lastname)
				.authority(Authority.ROLE_USER)
				.isDeleted(false).build();
	}

}
