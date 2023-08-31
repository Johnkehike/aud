package com.Auditionapp.Audition.Controller;


import com.Auditionapp.Audition.Entity.*;
import com.Auditionapp.Audition.Repository.ApplicantRepository;
import com.Auditionapp.Audition.Repository.EventsRepository;
import com.Auditionapp.Audition.Repository.UsersRepository;
import com.Auditionapp.Audition.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/web")
@Slf4j
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Value("${imagePath}")
    private String imagePath;

    @Value("${imageUploadPath}")
    private String imageUploadPath;



    @GetMapping("/dashboard")
    public String dashboardPage(Model model, HttpSession session) {


        Users userProfile = (Users) session.getAttribute("userprofile");
        int countEvents;
        int countApplicants;


        int countDirectors = usersRepository.countRoles2("DIRECTOR", userProfile.getFullName());
        int countProducers = usersRepository.countRoles2("PRODUCER", userProfile.getFullName());
        countApplicants = usersRepository.countRoles2("USER", userProfile.getFullName());
//        Applicants applicants = applicantRepository.findByEmail(userProfile.getEmail());

        if((String)session.getAttribute("userRole") == "USER") {
//            model.addAttribute("score", applicants.getApplicantScore());
//            model.addAttribute("status", applicants.getSelectionStatus());
            List<Applicants> events3 = applicantRepository.findEventsForApplicants(userProfile.getFullName());
            countEvents = events3.size();
            model.addAttribute("totalEvents", countEvents);

        }

        if((String)session.getAttribute("userRole") == "DIRECTOR") {
            List<Events> events = eventsRepository.findByDirectorUserId(userProfile.getFullName());
            countEvents = events.size();
            model.addAttribute("totalEvents", countEvents);

        }

        if((String)session.getAttribute("userRole") == "PRODUCER") {
            List<Events> events2 = eventsRepository.findEventsByProducer(userProfile.getFullName());
            countEvents = events2.size();
            model.addAttribute("totalEvents", countEvents);

        }

        if((String)session.getAttribute("userRole") == "ADMIN") {
            List<Events> events2 = eventsRepository.findAll();
            countEvents = events2.size();
            model.addAttribute("totalEvents", countEvents);

        }



//        if((String)session.getAttribute("userRole") == "USER") {
//            List<Applicants> events3 = applicantRepository.findEventsForApplicants(userProfile.getFullName());
//            countEvents = events3.size();
//            model.addAttribute("totalEvents", countEvents);
//
//        }



            model.addAttribute("countDirectors", countDirectors);
        model.addAttribute("countProducers", countProducers);
        model.addAttribute("countApplicants", countApplicants);

        model.addAttribute("role", (String)session.getAttribute("userRole"));

        return "Dashboard";
    }


    @PostMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody Users users, ResponseMessage response){

        Users users1 = usersRepository.findByName(users.getName());
        if(users1 == null) {
            response.setCode("96");
            response.setMessage("User does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        else {
            users1.setRole(Roles.valueOf(users.getRoleTest()));
            usersRepository.save(users1);

            response.setMessage("Updated Successfully");
            response.setCode("00");
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
    }


    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        String userName = (String) session.getAttribute("email");
        Users users = usersRepository.findByName(userName);
        users.setImagePath("/"+users.getName()+".jpg");
        model.addAttribute("users", users);

        return "profile";
    }


    @GetMapping("/password")
    public String ChangePassword(HttpSession session, Model model) {

        String userName = (String) session.getAttribute("email");

        return "changepassword";
    }



    @GetMapping("/viewDirectors")
    public String viewDirectors(Model model, HttpSession session) {

        String userRole = (String) session.getAttribute("userRole");
        Users userprofile = (Users) session.getAttribute("userprofile");

        List<Users> users = new ArrayList<>();
        if(userRole.equals("ADMIN")) {
          users = usersRepository.findAllRoles("DIRECTOR", userprofile.getFullName());
        }

        else if(userRole.equals("DIRECTOR")) {
            users = usersRepository.findAllRoles("PRODUCER", userprofile.getFullName());
        }

        else if(userRole.equals("PRODUCER")) {
            users = usersRepository.findAllRoles("USER", userprofile.getFullName());
        }

        model.addAttribute("allUsers", users);
        return "viewusers";
    }



    @GetMapping("/viewApplicants")
    public String viewApplicants(Model model, HttpSession session) {

        String userRole = (String) session.getAttribute("userRole");
        Users userProfile = (Users) session.getAttribute("userprofile");

        List<Applicants> applicants = new ArrayList<>();

        if(userRole.equals("DIRECTOR")) {
            applicants = applicantRepository.findApplicantsForDirectors(Status.valueOf("AUDITION"), userProfile.getFullName());
        }

        else if(userRole.equals("PRODUCER")) {
            applicants = applicantRepository.findApplicantsForProducers(Status.valueOf("AUDITION"), userProfile.getFullName());
        }

        model.addAttribute("allUsers", applicants);
        return "viewApplicants";
    }



    @GetMapping("/viewEvents")
    public String viewEvents(Model model, HttpSession session) {

        String userRole = (String) session.getAttribute("userRole");
        String userName = (String) session.getAttribute("user");
        String fullName = (String) session.getAttribute("userFullName");


        List<Events> events = new ArrayList<>();

        if(userRole.equals("DIRECTOR")) {
            events = eventsRepository.findByDirectorUserId(fullName);
        }

        else if(userRole.equals("PRODUCER")) {
            Users users = usersRepository.findByName(userName);
            events = eventsRepository.findEventsByProducer(users.getFullName());
        }

        else if(userRole.equals("USER")) {
            Users users = usersRepository.findByName(userName);
            events = eventsRepository.findEventsByApplicants(users.getFullName());
            log.info(events.toString());
        }

        String role = (String) session.getAttribute("userRole");
        model.addAttribute("userRole", role);

        model.addAttribute("allevents", events);
        return "viewevents";
    }



    @GetMapping("/{id}/delete")
    @Transactional
    public String DeleteUser(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {

        try {
            usersRepository.deleteByUserId(userId);
            redirectAttributes.addFlashAttribute("record", "User Deleted Successfully");
        }

        catch(Exception e) {
            redirectAttributes.addFlashAttribute("record", "Error deleting user");
        }
        return "redirect:/web/viewDirectors";

    }


    @GetMapping("/event/{id}/delete")
    @Transactional
    public String DeleteEvent(@PathVariable("id") Long eventId, RedirectAttributes redirectAttributes) {

        try {
            eventsRepository.deleteByEventId(eventId);
            redirectAttributes.addFlashAttribute("record", "Event Deleted Successfully");
        }

        catch(Exception e) {
            redirectAttributes.addFlashAttribute("record", "Error deleting Event");
        }
        return "redirect:/web/viewEvents";

    }


    @GetMapping("/create")
    public String createUser() {
        return "CreateUser";
    }


    @GetMapping("/createEvent")
    public String createEvent(Model model) {
//        List<Users> allUsers = usersRepository.findAllRoles("PRODUCER");
//        model.addAttribute("users", allUsers);
        return "CreateEvent";
    }



    @GetMapping("/sessionExpired")
    public RedirectView sessionExpired(HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return new RedirectView("/web/logout", true, false, true);
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes)
    {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logged out Successfully");
        return "redirect:/home";
    }



    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              Users users,
                              RedirectAttributes redirectAttributes, HttpSession session) {

        String userName = (String) session.getAttribute("email");
        users = usersRepository.findByName(userName);



//        if (!file.getContentType().equals("image/jpeg")) {
//            log.info("Only JPEG format is allowed.");
//            redirectAttributes.addFlashAttribute("success", "Only JPEG format is allowed.");
//
//            return "redirect:/web/profile";
//        }

        try {
            byte[] bytes = file.getBytes();
            String imagePath = imageUploadPath;
            Path directoryPath = Paths.get(imagePath);
            Files.createDirectories(directoryPath); // Create the directory if it doesn't exist

            String customFileName = users.getName() + ".jpg"; // Use custom name based on user's name
            Path filePath = directoryPath.resolve(customFileName);
            Files.write(filePath, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("File uploaded successfully!");
        redirectAttributes.addFlashAttribute("success", "Image Uploaded Successfully");
        return "redirect:/web/profile";
    }



    @GetMapping("/openjobs")
    public String applyForJobs(Model model) {
        List<Events> eventList =  eventsRepository.findEventsListByStatus("AUDITION");
        model.addAttribute("eventList", eventList);
        return "apply";
    }





}

