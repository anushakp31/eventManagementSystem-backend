package com.springboot.tracker.controller;

import com.springboot.tracker.entity.Event;
import com.springboot.tracker.entity.User;
import com.springboot.tracker.helpers.ZXingHelper;
import com.springboot.tracker.payload.EventDto;
import com.springboot.tracker.payload.deptUpdate;
import com.springboot.tracker.repository.DepartmentRepo;
import com.springboot.tracker.repository.EventRepo;
import com.springboot.tracker.repository.UserDepartmentRepo;
import com.springboot.tracker.repository.UserRepository;
import com.springboot.tracker.security.service.EventService;
import lombok.var;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping("/tracker/")
public class EventController {

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private UserDepartmentRepo userDepartmentRepo;
    @Autowired
    private UserRepository userRepository;
    private EventService eventService;
    @Autowired
    private DepartmentRepo departmentRepo;

    // Creating an event
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("events")
    public ResponseEntity<?> createEvent(@RequestBody EventDto eventDto) throws IOException, TimeoutException {
        long dept_id=eventDto.getDepartment().getId();
        boolean dept_exist=departmentRepo.existsById(dept_id);
        if(!dept_exist){
            return new ResponseEntity<>("Department does not exist",HttpStatus.OK);
        }
        String title;
        title=eventRepo.existsByEventTitle(eventDto.getTitle());
        if(eventDto.getTitle().equalsIgnoreCase(title)) {
            System.out.println("2"+eventDto.getTitle()+title);
            return new ResponseEntity<>("Event already created with the same title",HttpStatus.OK);

        }
        else {

            System.out.println(eventDto.getTitle()+title);
            return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.CREATED);
        }


    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("events")
    public List<EventDto> getAllEvents(){
        return eventService.getAllEvents();
    }

    @GetMapping("events/users")
    public List<EventDto> getAllEventsForUsers(){
        return eventService.getAllEventsForUsers();
    }

    @GetMapping("events/{id}")
    public ResponseEntity<EventDto> getEventByID(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(eventService.getEventById(id));
    }


    @GetMapping("events/usersPreference/{username}")
    public List<Event> getEventsForUserPreference(@PathVariable(name = "username") String username){
//        List<Long> dept_List=dept_ids.getDepartments();
        User user = userRepository.getUserFromUsername(username);
        List<Long> depts = userDepartmentRepo.getUserDepartmentsIdByUserId(user.getId());
        return  eventRepo.getAllEventsForUsersAccordingToPreference(depts);

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("events/{id}")
    public ResponseEntity<EventDto> updateEvent( @RequestBody EventDto eventDto,@PathVariable(name = "id") long id) throws IOException, TimeoutException {

        EventDto eventResponse=eventService.updateEventById(eventDto,id);
        return new ResponseEntity<>(eventResponse,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("events/{id}")
    public ResponseEntity<String> deleteEventById(@PathVariable(name = "id") long id) throws IOException, TimeoutException {

        eventService.deleteEventById(id);
        return new ResponseEntity<>("Event deleted successfully",HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users/{id}")
    public User getUserById(@PathVariable(name = "id") long id){
        User user =userRepository.findById(id);

        var userJson = new JSONObject(user);

        System.out.println(userJson);
        System.out.println(userJson.get("barcode"));
        return user;
    }
@RequestMapping(value = "barcode/{id}", method = RequestMethod.GET)
public void barcode(@PathVariable("id") long id, HttpServletResponse response) throws Exception {
    response.setContentType("image/png");
    OutputStream outputStream = response.getOutputStream();
    //outputStream.write(ZXingHelper.createBarCodeImage(id, 200, 200));
    byte[] bytes= userRepository.getBarcodeById(id);
    outputStream.write(bytes);
    outputStream.flush();
    outputStream.close();

}

}
