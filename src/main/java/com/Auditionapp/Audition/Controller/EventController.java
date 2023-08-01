package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.Events;
import com.Auditionapp.Audition.Entity.ResponseMessage;
import com.Auditionapp.Audition.Entity.Status;
import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Repository.EventsRepository;
import com.Auditionapp.Audition.Repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    EventsRepository eventsRepository;


    @PostMapping("/save")
    public ResponseEntity<ResponseMessage> profileUser(@RequestBody Events events, ResponseMessage responses,
                                                       HttpSession session) {


        log.info("Payload received is "+events);
//        List<String> producers = events.getProducerList();
//        String joinedString = String.join(", ", producers);

        Users userProfile = (Users) session.getAttribute("userprofile");
        events.setProducers(userProfile.getFullName());
        events.setDirectorUserId(userProfile.getCreatedBy());
        events.setStatus(Status.valueOf(events.getStatusTest()));

        try {
            eventsRepository.save(events);
            responses.setMessage("Event Saved Successfully");
            responses.setCode("00");
            return new ResponseEntity<>(responses, HttpStatus.OK);

        }
        catch(Exception e) {
            responses.setMessage("Error saving Event");
            responses.setCode("96");
            return new ResponseEntity<>(responses, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @PostMapping("/add-roles")
    public ResponseEntity<?> addRoles(@RequestBody Events events, ResponseMessage responseMessage) {

        log.info("The Event ID is "+events.getEventId());
        log.info("The Roles is "+events.getRoles());

        Optional<Events> events1 = eventsRepository.findById(events.getEventId());
        if(events1.isPresent()){
            if(events1.get().getRoles() != null) {
                events1.get().setRoles(events1.get().getRoles()+","+events.getRoles());
                events1.get().setRolesCount(events1.get().getRolesCount()+1);
                eventsRepository.save(events1.get());
                responseMessage.setCode("00");
                responseMessage.setMessage("Roles Successfully added");
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }

            else {
                events1.get().setRoles(events.getRoles());
                events1.get().setRolesCount(1);
                eventsRepository.save(events1.get());
                responseMessage.setCode("00");
                responseMessage.setMessage("Roles Successfully added");
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);

            }
        }


        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

    }


    @PostMapping("/findEventsByName")
    public ResponseEntity<?> findRolesByEvents(@RequestBody Events events) {

        Events event = eventsRepository.findByEventName(events.getEventName());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }


}
