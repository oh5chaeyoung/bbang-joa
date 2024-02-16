package com.sweetievegan.auth.dto.response;

import com.sweetievegan.auth.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
	private Long id;
	private String email;
	private String nickname;

	public static MemberResponse of(Member member) {
		return MemberResponse.builder()
				.id(member.getId())
				.email(member.getEmail())
				.nickname(member.getNickname())
				.build();
	}
}