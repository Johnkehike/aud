package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.Applicants;
import com.Auditionapp.Audition.Entity.ResponseMessage;
import com.Auditionapp.Audition.Entity.Roles;
import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Helpers.EmailSenderService;
import com.Auditionapp.Audition.Helpers.RandomGenertor;
import com.Auditionapp.Audition.Repository.ApplicantRepository;
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
import java.util.List;

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


    @PostMapping("/passwordchange")
    public ResponseEntity<ResponseMessage> changePassword(@RequestParam("password") String oldPassword,@RequestParam("newpassword") String newPassword,
                                 @RequestParam("newpassword1") String newPassword1, HttpSession session, ResponseMessage responses) {


        String userName = (String) session.getAttribute("email");
        Users user = usersRepository.findByName(userName);

        if (user != null || BCrypt.checkpw(oldPassword,user.getPassword())) {
            String hashed_password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(hashed_password);
            usersRepository.save(user);

            try {
                String htmlContent = "<html><body>" +
                        "<p>Hi " + user.getName() + ",</p>" +
                        "<p>Your password have been changed successfully on the Gian Carlo Auditioning app. </p>" +
                        "</body></html>";

                emailSenderService.sendEmail(user.getEmail(), "Password change update", htmlContent);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            responses.setCode("00");
            responses.setMessage("Password changed successfully");
            return new ResponseEntity<>(responses, HttpStatus.OK);
        }

        else {
            responses.setCode("90");
            responses.setMessage("Password change failed. Try again later");
            return new ResponseEntity<>(responses, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }


}
