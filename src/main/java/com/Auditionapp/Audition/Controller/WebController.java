package com.Auditionapp.Audition.Controller;


import com.Auditionapp.Audition.Entity.Events;
import com.Auditionapp.Audition.Entity.ResponseMessage;
import com.Auditionapp.Audition.Entity.Roles;
import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Repository.EventsRepository;
import com.Auditionapp.Audition.Repository.UsersRepository;
import com.Auditionapp.Audition.Repository.VisitorsRepository;
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
    private VisitorsRepository visitorsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Value("${imagePath}")
    private String imagePath;

    @Value("${imageUploadPath}")
    private String imageUploadPath;





    @GetMapping("/dashboard")
    public String dashboardPage(Model model, HttpSession session) {

        int countDirectors = usersRepository.countRoles("DIRECTOR");
        int countProducers = usersRepository.countRoles("PRODUCER");
        int countApplicants = usersRepository.countRoles("USER");

        model.addAttribute("countDirectors", countDirectors);
        model.addAttribute("countProducers", countProducers);
        model.addAttribute("countApplicants", countApplicants);

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



    @GetMapping("/viewDirectors")
    public String viewDirectors(Model model, HttpSession session) {

        String userRole = (String) session.getAttribute("userRole");

        List<Users> users = new ArrayList<>();
        if(userRole.equals("ADMIN")) {
          users = usersRepository.findAllRoles("DIRECTOR");
        }

        else if(userRole.equals("DIRECTOR")) {
            users = usersRepository.findAllRoles("PRODUCER");
        }

        else if(userRole.equals("PRODUCER")) {
            users = usersRepository.findAllRoles("USER");
        }

        model.addAttribute("allUsers", users);
        return "viewusers";
    }


    @GetMapping("/viewEvents")
    public String viewEvents(Model model, HttpSession session) {

        String userRole = (String) session.getAttribute("userRole");
        String userName = (String) session.getAttribute("user");

        List<Events> events = new ArrayList<>();

        if(userRole.equals("DIRECTOR")) {
            events = eventsRepository.findByDirectorUserId(userName);
        }

        else if(userRole.equals("PRODUCER")) {
            Users users = usersRepository.findByName(userName);
            events = eventsRepository.findEventsByProducer(users.getFullName());
        }

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
        List<Users> allUsers = usersRepository.findAllRoles("PRODUCER");
        model.addAttribute("users", allUsers);
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



        if (!file.getContentType().equals("image/jpeg")) {
            log.info("Only JPEG format is allowed.");
            redirectAttributes.addFlashAttribute("success", "Only JPEG format is allowed.");

            return "redirect:/web/profile";
        }

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




}

