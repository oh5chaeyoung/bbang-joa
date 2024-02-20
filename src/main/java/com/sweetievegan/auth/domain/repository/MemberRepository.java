package com.sweetievegan.auth.domain.repository;

import com.sweetievegan.auth.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	Optional<Member> findByEmail(String email);
	boolean existsByEmail(String email);

	Member findMemberById(String memberId);
}
