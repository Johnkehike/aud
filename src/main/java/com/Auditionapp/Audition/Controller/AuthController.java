package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.*;
import com.Auditionapp.Audition.Helpers.RandomGenertor;
import com.Auditionapp.Audition.Repository.EventsRepository;
import com.Auditionapp.Audition.Repository.OtpRepository;
import com.Auditionapp.Audition.Repository.UsersRepository;
import com.Auditionapp.Audition.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Controller
public class AuthController {



    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    ApplicantsController applicantsController;




    @GetMapping("/home")
    public String homePage() {
        return "Login";
    }

    @GetMapping("/")
    public String landingPage() {
        return "Login";
    }


    @GetMapping("/signup/{producer}")
    public String viewSignup(@PathVariable("producer") String producer, Model model, HttpSession session) {

        Users user = usersRepository.findByName(producer);
        String fullNameProducer = user.getFullName();
        List<Events> eventList =  eventsRepository.findEventsByProducerAndStatus(fullNameProducer, "AUDITION");

         model.addAttribute("producer", fullNameProducer);
         model.addAttribute("eventList", eventList);

        return "Signup";
    }


//    @PostMapping("/upload")
//    public String addNewUser(@RequestParam("file") MultipartFile file,
//                             @RequestParam("fname") String name, @RequestParam("surname") String surname,
//                             @RequestParam("phone") String phone, @RequestParam("logemail") String email,
//                             @RequestParam("road") int roadNumber, @RequestParam("plot") int plotNumber,
//                             @RequestParam("area") String area, @RequestParam("logpass") String userPassword, Users users,
//                             RedirectAttributes redirectAttributes) {
//
//        String houseAddress = area + " Road " + roadNumber + " Plot " + plotNumber;
//        System.out.println(houseAddress);
//        String hashed_password = BCrypt.hashpw(userPassword, BCrypt.gensalt());
//
//
//        String fullName = surname + " " + name;
//        users.setEmail(email);
//        users.setName(fullName);
//        users.setHouse_address(houseAddress);
//        users.setPhone_number(phone);
//        users.setPassword(hashed_password);
//        users.setRole(Roles.valueOf("USER"));
//
//
//        try {
//            userService.addNewUser(users);
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("success", "Chief! You already have an account. Please sign in");
//            return "redirect:/home";
//
//        }
//
//        if (!file.getContentType().equals("image/jpeg")) {
//            log.info("Only JPEG format is allowed.");
//            redirectAttributes.addFlashAttribute("success", "Only JPEG format is allowed.");
//
//            return "redirect:/home";
//        }
//
//        try {
//            byte[] bytes = file.getBytes();
//            String customFileName = fullName+".jpg";
//            Path path = Paths.get("src/main/resources/uploaded-files/" + customFileName);
//            Files.write(path, bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        log.info("File uploaded successfully!");
//
//        redirectAttributes.addFlashAttribute("success", "User Added Successfully");
//
//        return "redirect:/web/home";
//    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createNewUser(@RequestBody Users users, ResponseMessage responseMessage) {

        Users userDB = usersRepository.findByEmail(users.getEmail());

        if (userDB == null) {

            String hashed_password = BCrypt.hashpw(users.getPassword(), BCrypt.gensalt());
            users.setPassword(hashed_password);
            users.setRole(Roles.valueOf("USER"));

            usersRepository.save(users);
            responseMessage.setCode("00");
            responseMessage.setMessage("User Saved Successfully. Please login");
        }

        else {
            responseMessage.setCode("96");
            responseMessage.setMessage("User already exists on the system. Use a different User ID");

        }

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }




    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String email, @RequestParam("password") String userPassword, HttpSession session){

        Users user = usersRepository.findByName(email);

        if (user == null || !BCrypt.checkpw(userPassword,user.getPassword())) {
            // return an error response with status code 401 (Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } else {

            session.setAttribute("user", user.getName());
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("userRole", String.valueOf(user.getRole()));
            session.setAttribute("email", email);
            session.setAttribute("userprofile", user);
//            session.setAttribute("url", "/"+user.getName()+".jpg");
            // return a success response with status code 200 (OK)
            return ResponseEntity.ok("Login successful");
        }


    }


    @PostMapping("/validate-email")
    public ResponseEntity<?> validateEmail(@RequestBody Users users, ResponseMessage responseMessage, OtpModel otpModel) {

        Users userDB = usersRepository.findByEmail(users.getEmail());

        if (userDB == null) {
            log.info("Email does not exist");
            responseMessage.setCode("96");
            responseMessage.setMessage("Email does not exist");
        }

        else {
            log.info("Email exist and OTP will be sent");

            String otp = RandomGenertor.generateNumericRef(6);
            otpModel.setOtp(otp);
            otpModel.setStatus("Pending");
            otpModel.setUsername(userDB.getFullName());
            otpModel.setSendTime(LocalDateTime.now());
            otpModel.setEmail(users.getEmail());

            OtpModel otpModel1 = otpRepository.findByEmailAndStatus(userDB.getEmail(), "Pending");
            if(otpModel1 != null) {
                otpModel1.setStatus("Expired");
                otpRepository.save(otpModel1);
            }


            String otpMessageToUser = "<html><body>" +
                    "<p>Hi " + userDB.getFullName() + ",</p>" +
                    "<p>You tried resetting your password</p>" +
                    "<p>Please use below OTP to complete your password reset. </p>" +
                    "<p>OTP would become invalid in 10 minutes</p>"
                    +"<p><b>OTP: "+otp+"</b></p>"+
                    "</body></html>";

            applicantsController.SendMail(users.getEmail(), "Password Reset OTP",otpMessageToUser);
            otpRepository.save(otpModel);

            responseMessage.setCode("00");
            responseMessage.setMessage("OTP Sent to your email successfully");

        }
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);


    }


    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpModel otpModel, ResponseMessage responseMessage, Users users) {


        OtpModel otp = otpRepository.findByEmailAndStatus(otpModel.getEmail(), "Pending");

        if(otp == null) {
            responseMessage.setCode("99");
            responseMessage.setMessage("Invalid OTP");
        }

        else if(!otp.getStatus().equals("Pending")) {
            responseMessage.setCode("96");
            responseMessage.setMessage("OTP have been used");
        }


        else {
            Duration duration = Duration.between(LocalDateTime.now(), otp.getSendTime());
            long minutes = duration.toMinutes() % 60;

            if(minutes >= 10L) {
                responseMessage.setCode("97");
                responseMessage.setMessage("OTP have expired");
            }
            else {
                responseMessage.setCode("00");
                responseMessage.setMessage("Successfully validated OTP");
                otp.setStatus("Used");
                otp.setValidationTime(LocalDateTime.now());
                otpRepository.save(otp);
            }

        }

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }





    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Users users, ResponseMessage responseMessage) {


        Users users1 = usersRepository.findByEmail(users.getEmail());
        users1.setPassword(users.getPassword());
        usersRepository.save(users1);

        responseMessage.setCode("00");
        responseMessage.setMessage("Password changed. Please login.");

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }



}
