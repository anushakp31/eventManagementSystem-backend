package com.springboot.tracker.controller;

import com.springboot.tracker.entity.Department;
import com.springboot.tracker.entity.Role;
import com.springboot.tracker.entity.User;
import com.springboot.tracker.entity.UserDepartments;
import com.springboot.tracker.helpers.ZXingHelper;
import com.springboot.tracker.payload.LoginDto;
import com.springboot.tracker.payload.SignUpDto;
import com.springboot.tracker.payload.UserDepartmentDto;
import com.springboot.tracker.payload.deptUpdate;
import com.springboot.tracker.repository.DepartmentRepo;
import com.springboot.tracker.repository.RoleRepository;
import com.springboot.tracker.repository.UserDepartmentRepo;
import com.springboot.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/EventTracker/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private UserDepartmentRepo userDepartmentRepo;
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

    @GetMapping("/user/{username}")
    public ResponseEntity<UserDepartmentDto> getUserById(@PathVariable(name = "username") String username) {

        User user = userRepository.getUserFromUsername(username);
        List<Department> depts = userDepartmentRepo.getUserDepartmentsByUserId(user.getId());
        UserDepartmentDto uds = new UserDepartmentDto();
            uds.setUser(user);
            uds.setDeparments(depts);
        return new ResponseEntity<>(uds,HttpStatus.OK);
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<String> updateUserDepartment(@RequestBody deptUpdate updatedDepts, @PathVariable(name = "username") String username){
        List<Long> updatedDeptIds = updatedDepts.getDepartments();
        User user = userRepository.getUserFromUsername(username);
        List<Long> depts = userDepartmentRepo.getUserDepartmentsIdByUserId(user.getId());

        //dept dept=1,2,3
        //updated dept= 2,7,8
        for (int i = 0; i < updatedDeptIds.size() ; i++) {
            boolean result = depts.contains(updatedDeptIds.get(i));
            if(result){
                System.out.println("nothing to update");
            }else{
                Department dept = departmentRepo.getDeptById(updatedDeptIds.get(i));
                System.out.println("updating dept.");
                UserDepartments userDepartments = new UserDepartments();
                userDepartments.setUser(user);
                userDepartments.setUDepartment(dept);
                userDepartmentRepo.save(userDepartments);
                //7,8
            }
        }

        for (int i = 0; i < depts.size() ; i++) {
            boolean result = updatedDeptIds.contains(depts.get(i));
            if(result){
                System.out.println("do nothing");
            }
            else{
                Department dept = departmentRepo.getDeptById(depts.get(i));
                System.out.println("deleting dept."+depts.get(i));
                System.out.println("user is"+user.getId());
                UserDepartments userDepartments = new UserDepartments();
                userDepartments.setUser(user);
                userDepartments.setUDepartment(dept);
                userDepartmentRepo.getUserDepartmentsIdByUserId(user.getId(), depts.get(i));            }

        }

        return new ResponseEntity<>("preference updated",HttpStatus.OK);
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
        Long id=user.getId();
        String id1=id.toString();
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        byte[] barcode= ZXingHelper.createBarCodeImage(id1,200,100);
        user.setBarcode(barcode);

        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singleton(roles));
        userRepository.save(user);
        List<Long> departments = signUpDto.getDepartments();

        for (int i = 0; i < departments.size(); i++) {
            UserDepartments userDepartments = new UserDepartments();
            userDepartments.setUser(user);
            Department dept = departmentRepo.getDeptById(departments.get(i));
            userDepartments.setUDepartment(dept);
            userDepartmentRepo.save(userDepartments);

        }

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }


}
