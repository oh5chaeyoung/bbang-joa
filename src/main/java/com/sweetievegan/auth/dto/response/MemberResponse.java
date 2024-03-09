package com.sweetievegan.auth.dto.response;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.util.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
	private String id;
	private String email;
	private String nickname;
	private String firstname;
	private String lastname;
	private String profile;
	private String summary;
	private Authority authority;

	public static MemberResponse of(Member member) {
		return MemberResponse.builder()
				.id(member.getId())
				.email(member.getEmail())
				.nickname(member.getNickname())
				.firstname(member.getFirstname())
				.lastname(member.getLastname())
				.profile(member.getProfile())
				.summary(member.getSummary())
				.authority(member.getAuthority())
				.build();
	}
}