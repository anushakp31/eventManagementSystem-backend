package com.springboot.tracker.controller;

import com.springboot.tracker.entity.Role;
import com.springboot.tracker.entity.User;
import com.springboot.tracker.helpers.ZXingHelper;
import com.springboot.tracker.payload.LoginDto;
import com.springboot.tracker.payload.SignUpDto;
import com.springboot.tracker.repository.RoleRepository;
import com.springboot.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/EventTracker/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/Login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
         Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>("User logged-in successfully!.", HttpStatus.OK);
    }

    @PostMapping("/SignUp")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        //add check for username exists in a database
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        //add check for email exists in a database
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email already exists!!", HttpStatus.BAD_REQUEST);
        }
        //create user object
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setAddress(signUpDto.getAddress());
        user.setPhNo(signUpDto.getPhNo());
        user.setZipcode(signUpDto.getZipcode());
        user.setUtaId(signUpDto.getUtaId());
        Long utaId=user.getUtaId();
        String utaId1=utaId.toString();
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        byte[] barcode= ZXingHelper.createBarCodeImage(utaId1,200,100);
        user.setBarcode(barcode);
        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }


}
