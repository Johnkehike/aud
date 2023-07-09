package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.Applicants;
import com.Auditionapp.Audition.Entity.Roles;
import com.Auditionapp.Audition.Entity.Users;
import com.Auditionapp.Audition.Helpers.EmailSenderService;
import com.Auditionapp.Audition.Helpers.RandomGenertor;
import com.Auditionapp.Audition.Repository.ApplicantRepository;
import com.Auditionapp.Audition.Repository.UsersRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/applicants")
@Slf4j
public class ApplicantsController {


    @Value("${signUpPage}")
    private String signUpPage;

    @Autowired
    ApplicantRepository applicantRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Value("${homePage}")
    private String homePage;



    @Value("${applicantUploadPath}")
    private String applicantUploadPath;


    @PostMapping("/getUrl")
    public ResponseEntity<Map<String, Object>> GenerateUrlForApplicants(HttpSession session) throws IOException, WriterException {

        log.info("Generating URL right now");
        Map<String, Object> responseBody = new HashMap<>();
        String loggedProducer = (String) session.getAttribute("user");

        String data = signUpPage+loggedProducer;
        String message = "Please share this URL with prospective applicants. You can also share QR code below \n"+data;

        int width = 300;
        int height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] imageBytes = pngOutputStream.toByteArray();

        // create response entity
        responseBody.put("qrCodeImage", imageBytes);
        responseBody.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }


    @PostMapping("/upload")
    public String addApplicants(@RequestParam("files") List<MultipartFile> files,
                                @RequestParam("first_name") String fname, @RequestParam("last_name") String lastName,
                                @RequestParam("email") String email, @RequestParam("events") String events,
                                @RequestParam("producer") String producer, @RequestParam("date") String dateOfEvent,
                                @RequestParam("area_code") String areaCode, @RequestParam("phone") String phone,
                                @RequestParam("password") String password,
                                @RequestParam("roles") String roleApplied, Applicants applicants, Users users,
                                RedirectAttributes redirectAttributes) {

        String fullName = fname+" "+lastName;
        String hashed_password = BCrypt.hashpw(password, BCrypt.gensalt());
        String id = RandomGenertor.generateNumericRef(3);


        try {
            for(MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String imagePath = applicantUploadPath+events+"/"+fullName;
                Path directoryPath = Paths.get(imagePath);
                Files.createDirectories(directoryPath); // Create the directory if it doesn't exist

                String customFileName = file.getOriginalFilename() + ".jpg"; // Use custom name based on user's name
                Path filePath = directoryPath.resolve(customFileName);
                Files.write(filePath, bytes);
                log.info("Files Successfully uploaded to the drive");
            }

            applicants.setApplicantRole(roleApplied);
            applicants.setApplicantName(fullName);
            applicants.setEventName(events);
            applicants.setProducerName(producer);
            applicants.setEmail(email);
            applicants.setPhone(areaCode+phone);

            users.setPassword(hashed_password);
            users.setRole(Roles.valueOf("USER"));
            users.setPhone_number(areaCode+phone);
            users.setEmail(email);
            users.setFullName(fullName);
            users.setName(fullName.replace(" ", "").toLowerCase()+id);

            Users users1 = usersRepository.save(users);
            applicants.setApplicantId(users1.getUserId());
            applicantRepository.save(applicants);

            SendMail(email, users.getName());

            redirectAttributes.addFlashAttribute("status", "Records Saved Successfully. Please login");
            return "redirect:/home";

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("status", "Failed to save records, try again");
            return "redirect:/signup/"+producer;
        }

    }


    public void SendMail(String recipientEmail, String recipientName) {


        try {
            String htmlContent = "<html><body>" +
                    "<p>Hi " + recipientName + ",</p>" +
                    "<p>You have been profiled successfully on Gian Carlo Auditioning app. </p>" +
                    "<p>Please use your User ID and password below to login </p>"
                    +"<p> You can login using this url "+ homePage + "</p>"+
                    "</body></html>";

            emailSenderService.sendEmail(recipientEmail, "Profile Creation Update", htmlContent);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


}
