package com.sweetievegan.auth.domain.repository;

import com.sweetievegan.auth.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	@Query("SELECT m FROM Member m WHERE m.email = :email and m.isDeleted = false")
	Optional<Member> findMemberByEmail(String email);
	boolean existsByEmail(String email);
	@Query("SELECT m FROM Member m WHERE m.id = :memberId and m.isDeleted = false")
	Member findMemberById(String memberId);
}
