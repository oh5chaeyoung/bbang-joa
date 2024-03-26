package com.sweetievegan.auth.domain.entity;

import com.sweetievegan.auth.util.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "members")
public class Member {
	@Id
	@Column(name = "member_id")
	private String id;

	private String email;
	private String nickname;
	private String firstname;
	private String lastname;
	private String password;
	private boolean isDeleted;

	private String profile;
	private String summary;
	private String provider;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@CreatedDate
	private LocalDateTime createDate;

	public void setId(String id) {
		this.id = id;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public void setPassword(String password){this.password = password;}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	public void setSummary(String summary) { this.summary = summary; }

	public Member update(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public void deleteMember() {
		this.isDeleted = true;
	}
}
