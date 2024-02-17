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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;
	private String nickname;
	private String firstname;
	private String lastname;
	private String password;
	private boolean isDeleted;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@CreatedDate
	private LocalDateTime createDate;

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public void setPassword(String password){this.password = password;}

}
