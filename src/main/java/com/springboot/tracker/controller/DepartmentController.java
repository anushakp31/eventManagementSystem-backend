package com.springboot.tracker.controller;

import com.springboot.tracker.entity.Department;
import com.springboot.tracker.entity.User;
import com.springboot.tracker.payload.DepartmentDto;
import com.springboot.tracker.repository.DepartmentRepo;
import com.springboot.tracker.repository.UserRepository;
import com.springboot.tracker.security.service.DepartmentService;
import lombok.var;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/departments/")
public class DepartmentController {

    @Autowired
    private DepartmentRepo departmentRepo;
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    private DepartmentService departmentService;


    //Creating an event
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentDto departmentDto){
        String name;
        name=departmentRepo.existsDepartmentByName(departmentDto.getName());
        if(departmentDto.getName().equalsIgnoreCase(name)) {
            System.out.println("2"+departmentDto.getName()+name);
            return new ResponseEntity<>("Department already created",HttpStatus.OK);

        }
        else {

            System.out.println(departmentDto.getName()+name);
            return new ResponseEntity<>(departmentService.createDepartment(departmentDto), HttpStatus.CREATED);
        }
        }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<DepartmentDto> getAllDepartments(){

        return departmentService.getAllDepartments();
    }
    @GetMapping("/users")
    public List<Department> getAllDepartmentsForUsers(){
        return departmentRepo.getAllDepartmentsForUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartmentById(@RequestBody DepartmentDto departmentDto,@PathVariable(name = "id") long id){

        DepartmentDto departmentResponse= departmentService.updateDepartmentById(departmentDto,id);
        return new ResponseEntity<>(departmentResponse,HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDepartmentById(@PathVariable(name = "id") long id) throws IOException, TimeoutException {

        departmentService.deleteDepartmentById(id);
        return new ResponseEntity<>("Department deleted successfully",HttpStatus.OK);
    }


}

