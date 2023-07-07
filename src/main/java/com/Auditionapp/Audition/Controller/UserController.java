package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.ResponseMessage;
import com.Auditionapp.Audition.Entity.Roles;
import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Helpers.EmailSenderService;
import com.Auditionapp.Audition.Helpers.RandomGenertor;
import com.Auditionapp.Audition.Repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Value("${imageUploadPath}")
    private String imageUploadPath;

    @Value("${homePage}")
    private String homePage;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage> profileUser(@RequestBody Users users, ResponseMessage responses) {

        if(users.getRoleTest().equals("THEATRE DIRECTOR")) {
            users.setRoleTest("DIRECTOR");
        }

        users.setFullName(users.getName());
        String id = RandomGenertor.generateNumericRef(3);
        users.setName(users.getName().replace(" ", "").toLowerCase()+id);

        String rawPassword = RandomGenertor.generateRandomString("");
        String customFileName = imageUploadPath+users.getName()+".jpg";

        users.setImagePath(customFileName);

        String hashed_password = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        users.setPassword(hashed_password);

        users.setRole(Roles.valueOf(users.getRoleTest()));
        try {
            usersRepository.save(users);
            responses.setMessage("User Saved Successfully");
            responses.setCode("00");
            SendMail(users.getEmail(), users.getName(), rawPassword);
            return new ResponseEntity<>(responses, HttpStatus.OK);

        }
        catch(Exception e) {
            responses.setMessage("Error saving user");
            responses.setCode("96");
            return new ResponseEntity<>(responses, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    public void SendMail(String recipientEmail, String recipientName, String password) {


        try {
            String htmlContent = "<html><body>" +
                    "<p>Hi " + recipientName + ",</p>" +
                    "<p>You have been profiled successfully on Gian Carlo Auditioning app. </p>" +
                    "<p>Please use your Email ID and password below to login </p>"
                    +"<p><b> " + password  +"</b></p>"+
                    "<p> You can login using this url "+ homePage + "</p>"+
                    "</body></html>";

            emailSenderService.sendEmail(recipientEmail, "Profile Creation Update", htmlContent);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }



}
