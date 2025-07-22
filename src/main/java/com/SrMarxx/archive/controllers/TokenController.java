package com.SrMarxx.archive.controllers;


import com.SrMarxx.archive.business.models.entities.RoleEntity;
import com.SrMarxx.archive.business.models.repositories.IUserJpaRepository;
import com.SrMarxx.archive.controllers.dtos.LoginRequestRecordDTO;
import com.SrMarxx.archive.controllers.dtos.LoginResponseRecordDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;



@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final IUserJpaRepository iUserJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenController(JwtEncoder jwtEncoder, IUserJpaRepository iUserJpaRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.iUserJpaRepository = iUserJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseRecordDTO> login(@RequestBody LoginRequestRecordDTO loginRequestRecordDTO){
        var user = iUserJpaRepository.findByUsername(loginRequestRecordDTO.username());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequestRecordDTO, passwordEncoder)){
            throw new BadCredentialsException("User or Password is invalid!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles()
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("legendai")
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseRecordDTO(jwtValue, expiresIn));
    }
}
