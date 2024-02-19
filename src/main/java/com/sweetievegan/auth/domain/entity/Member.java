package com.sweetievegan.auth.domain.entity;

import com.sweetievegan.auth.util.Authority;
import com.sweetievegan.blog.domain.entity.BlogImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

	@Column(nullable = true)
	private String profile;

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
}
