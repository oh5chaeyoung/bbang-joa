package com.sweetievegan.domain.service;

import com.sweetievegan.domain.dto.MemberDto;

import java.util.List;

public interface MemberService {
    Long registerMember(MemberDto memberDto);
    Long updateMemberDetail(Long memberId, MemberDto memberDto);
    Long removeMember(Long memberId);
}
