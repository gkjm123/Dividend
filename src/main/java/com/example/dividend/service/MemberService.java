package com.example.dividend.service;

import com.example.dividend.exception.impl.AlreadyExistUserException;
import com.example.dividend.model.Auth;
import com.example.dividend.persist.MemberRepository;
import com.example.dividend.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    final PasswordEncoder passwordEncoder;
    final private MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return memberRepository.findByName(name)
                .orElseThrow( () -> new UsernameNotFoundException("user not found"));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByName(member.getUsername());

        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        MemberEntity memberEntity = member.toEntity();
        return memberRepository.save(memberEntity);
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity memberEntity = memberRepository.findByName(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!passwordEncoder.matches(member.getPassword(), memberEntity.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return memberEntity;
    }
}
