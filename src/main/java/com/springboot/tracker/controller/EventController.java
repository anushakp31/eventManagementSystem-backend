package com.springboot.tracker.controller;

import com.springboot.tracker.entity.User;
import com.springboot.tracker.helpers.ZXingHelper;
import com.springboot.tracker.payload.EventDto;
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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/tracker/")
public class EventController {

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired
    private UserRepository userRepository;
    private EventService eventService;

    // Creating an event
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("events")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto){
        return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.CREATED);
    }

    @GetMapping("events")
    public List<EventDto> getAllEvents(){
        return eventService.getAllEvents();
    }

    @GetMapping("events/{id}")
    public ResponseEntity<EventDto> getEventByID(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(eventService.getEventById(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("events/{id}")
    public ResponseEntity<EventDto> updateEvent( @RequestBody EventDto eventDto,@PathVariable(name = "id") long id){

        EventDto eventResponse=eventService.updateEventById(eventDto,id);
        return new ResponseEntity<>(eventResponse,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("events/{id}")
    public ResponseEntity<String> deleteEventById(@PathVariable(name = "id") long id){

        eventService.deleteEventById(id);
        return new ResponseEntity<>("Event deleted successfully",HttpStatus.OK);
    }
//    @RequestMapping(value = "barcode/{id}", method = RequestMethod.GET)
//    public void barcode(@PathVariable("id") String id, HttpServletResponse response) throws Exception {
//        response.setContentType("image/png");
//        OutputStream outputStream = response.getOutputStream();
//        outputStream.write(ZXingHelper.createBarCodeImage(id, 200, 100));
//        outputStream.write(id.getBytes());
//        outputStream.flush();
//        outputStream.close();
//    }
//    @RequestMapping(value = "barcode/{id}", method = RequestMethod.GET)
//    public void barcode(@PathVariable("id") int id, HttpServletResponse response) throws Exception {
//        response.setContentType("image/png");
//        OutputStream outputStream = response.getOutputStream();
//        User user =userRepository.findById(id);
//
//        var userJson = new JSONObject(user);
//
//        System.out.println(userJson);
//        System.out.println(userJson.get("barcode"));
////        byte[] barcode1=byte[]()
//        System.out.println(userJson.get("barcode").getClass());
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        ObjectOutputStream oos = new ObjectOutputStream(bos);
////        oos.writeObject(userJson.get("barcode"));
//        //oos.flush();
//        JSONArray jsonArray=new JSONArray(userJson.get("barcode").toString());
//        byte[] bytes = new byte[jsonArray.length()];
//        for (int i = 0; i < jsonArray.length(); i++) {
//            bytes[i]=(byte)(((int)jsonArray.get(i)) & 0xFF);
//        }
//        System.out.println(bytes);
//        outputStream.write(bytes);
////        outputStream.write(ZXingHelper.createBarCodeImage(id, 200, 100));
////        outputStream.write(id.getBytes());
//        outputStream.flush();
//        outputStream.close();
//        //outputStream.write(userRepository.g);
//
//    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users/{id}")
    public User getUserById(@PathVariable(name = "id") long id){
        User user =userRepository.findById(id);

        var userJson = new JSONObject(user);

        System.out.println(userJson);
        System.out.println(userJson.get("barcode"));
        return user;
    }
//    @PostMapping("events")
//    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto eventDto){
//        return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.CREATED);
//    }

}