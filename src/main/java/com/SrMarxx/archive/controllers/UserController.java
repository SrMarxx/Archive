package com.SrMarxx.archive.controllers;


import com.SrMarxx.archive.business.models.entities.RoleEntity;
import com.SrMarxx.archive.business.models.entities.UserEntity;
import com.SrMarxx.archive.business.models.repositories.IRoleJpaRepository;
import com.SrMarxx.archive.business.models.repositories.IUserJpaRepository;
import com.SrMarxx.archive.business.services.UserService;
import com.SrMarxx.archive.controllers.dtos.UserRecordDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    IUserJpaRepository userRepository;

    @Autowired
    IRoleJpaRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> newUser(@RequestBody @Valid UserRecordDTO userRecordDTO) {

        var userFromDb = userRepository.findByUsername(userRecordDTO.username());
        if (userFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        var basicRole = roleRepository.findByName(RoleEntity.Values.BASIC.name());

        var userEntity = new UserEntity();
        userEntity.setName(userRecordDTO.name());
        userEntity.setEmail(userRecordDTO.email());
        userEntity.setUsername(userRecordDTO.username());
        userEntity.setPassword(passwordEncoder.encode(userRecordDTO.password()));
        userEntity.setRoles(Set.of(basicRole));

        userService.save(userEntity);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserEntity>> getUser(){
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

}
