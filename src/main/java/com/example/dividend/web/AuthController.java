package com.example.dividend.web;

import com.example.dividend.model.Auth;
import com.example.dividend.persist.MemberRepository;
import com.example.dividend.persist.entity.MemberEntity;
import com.example.dividend.security.TokenProvider;
import com.example.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity memberEntity = memberService.register(request);
        return ResponseEntity.ok(memberEntity);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity memberEntity = memberService.authenticate(request);
        String token = tokenProvider.generateToken(memberEntity.getName(), memberEntity.getRoles());
        return ResponseEntity.ok(token);
    }
}
