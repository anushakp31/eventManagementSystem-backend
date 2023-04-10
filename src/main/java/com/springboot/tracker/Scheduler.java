package com.springboot.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.springboot.tracker.entity.Event;
import com.springboot.tracker.payload.DepartmentDto;
import com.springboot.tracker.payload.EventDto;
import com.springboot.tracker.repository.DepartmentRepo;
import com.springboot.tracker.repository.EventRepo;
import com.springboot.tracker.repository.UserEventRepo;
import com.springboot.tracker.security.service.DepartmentService;
import com.springboot.tracker.security.service.UserEventsService;
import org.hibernate.Hibernate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Date;
import java.time.LocalDateTime;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeoutException;



@Component
    public class Scheduler {

    public static void sendMessageToBroker(String userId, String data, String routingKey) throws IOException, TimeoutException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("$host");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
//        System.out.println("routing key is"+routingKey);
        Channel channel = connection.createChannel();
        if (routingKey == null){
            System.out.println("sending emergency notifcation");
            channel.exchangeDeclare("public", BuiltinExchangeType.FANOUT);
            channel.basicPublish("public","",null, data.getBytes(StandardCharsets.UTF_8));
        } else {

            System.out.println("sending user specific notif");
            channel.exchangeDeclare("user",BuiltinExchangeType.DIRECT);
            channel.basicPublish("user",userId,null,data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void sendGenericMessagetoBroker(String deptName, String data, String routingKey) throws IOException, TimeoutException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("18.219.9.194");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
//        System.out.println("routing key is"+routingKey);
        Channel channel = connection.createChannel();

//            System.out.println("sending user specific notif");
            channel.exchangeDeclare(deptName,BuiltinExchangeType.FANOUT);
            channel.basicPublish(deptName,"",null,data.getBytes(StandardCharsets.UTF_8));
        }



    public Scheduler(DepartmentRepo departmentRepo, EventRepo eventRepo, DepartmentService departmentService, UserEventRepo UserEventRepo ) {
        this.departmentRepo = departmentRepo;
        this.eventRepo = eventRepo;
        this.departmentService = departmentService;
        this.userEventRepo = UserEventRepo;
    }
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserEventRepo userEventRepo;

    @Scheduled(initialDelay = 1000, fixedRate = 10000)
    public void cronJobSch() throws IOException, TimeoutException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, +2);
        java.util.Date now = calendar.getTime();
        List<Long> event_ids = eventRepo.getId(now);
        List<String> titles = eventRepo.getEventName(now);
        List<String> venue = eventRepo.getVenue(now);
        List<String> description = eventRepo.getDescription(now);


        for (int i = 0; i < event_ids.size(); i++) {

            List<Long> user_ids = userEventRepo.getUsers(event_ids.get(i),false);
            for (int j = 0; j < user_ids.size(); j++) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("title", titles.get(i));
                data.put("description", description.get(i));
                data.put("venue", venue.get(i));
                data.put("event_id", event_ids.get(i).toString());

                String json = new ObjectMapper().writeValueAsString(data);

                try {
                    sendMessageToBroker(user_ids.get(j).toString(), json, user_ids.get(j).toString());
                    userEventRepo.updateUserEvent(true, user_ids.get(j), event_ids.get(i));

                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }

//        try {
////            sendMessageToBroker("1","","","","mav alert");
//        } catch (IOException | TimeoutException e) {
//            throw new RuntimeException(e);
//        }



    }

    }


}