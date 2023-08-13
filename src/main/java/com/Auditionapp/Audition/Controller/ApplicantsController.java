package com.Auditionapp.Audition.Controller;

import com.Auditionapp.Audition.Entity.*;
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
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    @Value("${openJobs}")
    private String openJobs;

    @Value("${applicantUploadPath}")
    private String applicantUploadPath;

    @Value("${applicantsUploadPath}")
    private String applicantsUploadPath;


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
                                @RequestParam("message") String message,
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

        Applicants applicants1 = applicantRepository.findByEmail(email);
        Users userCheck = usersRepository.findByFullName(producer);
        Users checkApplicant = usersRepository.findByEmail(email);
        if(applicants1 != null) {
            redirectAttributes.addFlashAttribute("status", "Applicant already applied for an event");
            return "redirect:/signup/"+producer;
        }

        if(checkApplicant != null) {
            redirectAttributes.addFlashAttribute("status", "Applicant email already exists");
            return "redirect:/signup/"+producer;
        }

        try {
            for(MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String imagePath = applicantUploadPath+events+"/"+fullName;
                Path directoryPath = Paths.get(imagePath);
                Files.createDirectories(directoryPath); // Create the directory if it doesn't exist

//                String customFileName = file.getOriginalFilename() + getExtension(file.getOriginalFilename());
                String customFileName = file.getOriginalFilename();
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
            applicants.setMessage(message);
            applicants.setSelectionStatus(ApplicantSelection.valueOf("PENDING"));
            applicants.setTheaterDirector(userCheck.getCreatedBy());

            users.setPassword(hashed_password);
            users.setRole(Roles.valueOf("USER"));
            users.setPhone_number(areaCode+phone);
            users.setEmail(email);
            users.setFullName(fullName);
            users.setName(fullName.replace(" ", "").toLowerCase()+id);
            users.setCreatedBy(userCheck.getFullName());
            users.setDateCreated(LocalDateTime.now());

            applicants.setUserName(users.getName());

            Users users1 = usersRepository.save(users);

            applicants.setUserId(users1.getUserId());
            applicants.setDateApplied(LocalDateTime.now());
            applicantRepository.save(applicants);

            String htmlContent = "<html><body>" +
                    "<p>Dear " + users.getFullName() + ",</p>" +
                    "<p>You have successfully applied to: </p>" +
                    "<p>Producer: "+applicants.getProducerName()+"</p>" +
                    "<p>Event: "+applicants.getEventName()+"</p>" +
                    "<p>Premier Date: "+dateOfEvent+"</p>" +
                    "<p>Role: "+roleApplied+"</p>" +
                    "<p>You submitted "+files.size()+" files in total</p>" +
                    "<p>Your username is "+users.getName()+" </p>" +
                    "<p>Your password is "+password+" </p>" +
                    "<p>Please upload a personal picture on our portal https://www.myauditions.us</p>" +
                    "<p>Producer "+applicants.getProducerName()+ " will email you with any news about this event and your application</p>"
                    +"<p>Thank you for using our platform "+ homePage + "</p>"+
                    "<p>Feel free to use this URL "+ openJobs + " to explore more possibilities for you and your career</p>"+
                    "</body></html>";

            String htmlContentToProducer = "<html><body>" +
                    "<p>Hi " + producer + ",</p>" +
                    "<p>You have a new applicant applying for the role of "+roleApplied+". </p>" +
                    "<p>Please see message sent to you below by the applicant and login to view uploaded files </p>"
                    +"<p><b>"+message+"</b></p>"+
                    "</body></html>";

            try {
                SendMail(email, "Profile Creation Update", htmlContent);
                SendMail(userCheck.getEmail(), "Application Notification", htmlContentToProducer);
            }
            catch(Exception e) {
                log.info("Exception experienced is "+e.getMessage());
            }

            redirectAttributes.addFlashAttribute("status", "Records Saved Successfully. Please login");
            return "redirect:/home";

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("status", "Failed to save records, try again");
            return "redirect:/signup/"+producer;
        }

    }




    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        } else {
            return "";
        }

    }




    public void SendMail(String recipientEmail, String subject, String htmlContent) {


        try {
            emailSenderService.sendEmail(recipientEmail, subject, htmlContent);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadApplicantsData(@RequestParam("eventName") String eventName, @RequestParam("applicantName") String applicantName) {

//        String folderPath = "C:" + File.separator + eventName + File.separator + applicantName;
        String folderPath = "/home3/myauditi/etc/myauditions.us/" + eventName + "/" + applicantName;

        try {
            // Create a temporary file to store the zipped folder
            File zipFile = File.createTempFile("temp", ".zip");
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Recursively add files and subdirectories to the zip file
            addFolderToZip(zos, Paths.get(folderPath), Paths.get(folderPath).getFileName().toString());

            zos.close();
            fos.close();

            // Read the zipped file into a byte array
            byte[] zipBytes = Files.readAllBytes(zipFile.toPath());

            // Set the appropriate headers for the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("user_folder.zip").build());

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addFolderToZip(ZipOutputStream zos, Path folderPath, String baseFolderName) throws IOException {
        File folder = folderPath.toFile();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addFolderToZip(zos, file.toPath(), baseFolderName + File.separator + file.getName());
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(baseFolderName + File.separator + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                    fis.close();
                }
            }
        }
    }



    @PostMapping("/scores")
    @Transactional
    public ResponseEntity<ResponseMessage> updateScores(@RequestBody Applicants applicants, ResponseMessage responseMessage) {
        try {
            Applicants applicants1 = applicantRepository.findByApplicantId(applicants.getApplicantId());
            int newScore = applicants1.getApplicantScore() + applicants.getScore();

            applicants1.setApplicantScore(newScore);
            applicants1.setSelectionStatus(ApplicantSelection.valueOf(applicants.getApplicantStatus()));

            applicantRepository.save(applicants1);

            responseMessage.setMessage("Score updated successfully");
            responseMessage.setCode("00");

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }

        catch(Exception e){

            responseMessage.setMessage("Failed to update score");
            responseMessage.setCode("96");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}/delete")
    @Transactional
    public String DeleteApplicant(@PathVariable("id") Long applicationId, RedirectAttributes redirectAttributes) {

        try {
            Applicants applicants = applicantRepository.findByApplicantId(applicationId);
            applicantRepository.deleteByApplicantId(applicationId);
            usersRepository.deleteByUserId(applicationId - 1L);

            String folderPath = applicantsUploadPath + File.separator + applicants.getEventName() + File.separator + applicants.getApplicantName();
            Path directoryPath = Paths.get(folderPath);

            try {
                Files.walk(directoryPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                System.out.println("Folder deleted successfully");
            } catch (IOException e) {
                System.err.println("Failed to delete folder: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("record", "Applicant Deleted Successfully");
        }

        catch(Exception e) {
            redirectAttributes.addFlashAttribute("record", "Error deleting applicant");
        }
        return "redirect:/web/viewApplicants";

    }


    @PostMapping("/apply")
    public String applyApplicant(@RequestParam("files") List<MultipartFile> files,
                                @RequestParam("message") String message,
                                @RequestParam("events") String events,
                                @RequestParam("producer") String producer, @RequestParam("date") String dateOfEvent,
                                @RequestParam("roles") String roleApplied, Applicants applicants,
                                RedirectAttributes redirectAttributes, HttpSession session) {

        String id = RandomGenertor.generateNumericRef(3);
        Users userProfile = (Users) session.getAttribute("userprofile");


        Applicants applicants1 = applicantRepository.findByEmailAndEventName(userProfile.getEmail(),events);
        Users userCheck = usersRepository.findByFullName(producer);
        Users checkApplicant = usersRepository.findByEmail(userProfile.getEmail());
        if(applicants1 != null) {
            redirectAttributes.addFlashAttribute("status", "You applied for the "+events+ " event. Please apply for a different event");
            return "redirect:/web/openjobs";
        }

        if(checkApplicant == null) {
            redirectAttributes.addFlashAttribute("status", "Please create an account first");
            return "redirect:/signup/"+producer;
        }

        try {
            for(MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String imagePath = applicantUploadPath+events+"/"+checkApplicant.getFullName();
                Path directoryPath = Paths.get(imagePath);
                Files.createDirectories(directoryPath); // Create the directory if it doesn't exist

//                String customFileName = file.getOriginalFilename() + getExtension(file.getOriginalFilename());
                String customFileName = file.getOriginalFilename();
                Path filePath = directoryPath.resolve(customFileName);


                Files.write(filePath, bytes);
                log.info("Files Successfully uploaded to the drive");
            }

            applicants.setApplicantRole(roleApplied);
            applicants.setApplicantName(checkApplicant.getFullName());
            applicants.setEventName(events);
            applicants.setProducerName(producer);
            applicants.setEmail(checkApplicant.getEmail());
            applicants.setPhone(checkApplicant.getPhone_number());
            applicants.setMessage(message);
            applicants.setSelectionStatus(ApplicantSelection.valueOf("PENDING"));
            applicants.setTheaterDirector(userCheck.getCreatedBy());
            applicants.setDateApplied(LocalDateTime.now());


            applicants.setUserName(checkApplicant.getName());

            applicants.setUserId(checkApplicant.getUserId());
            applicantRepository.save(applicants);

            String htmlContent = "<html><body>" +
                    "<p>Dear " + userProfile.getFullName() + ",</p>" +
                    "<p>You have successfully applied to: </p>" +
                    "<p>Producer: "+applicants.getProducerName()+"</p>" +
                    "<p>Event: "+applicants.getEventName()+"</p>" +
                    "<p>Premier Date: "+dateOfEvent+"</p>" +
                    "<p>Role: "+roleApplied+"</p>" +
                    "<p>You submitted "+files.size()+" files in total</p>" +
                    "<p>Please upload a personal picture on our portal https://www.myauditions.us</p>" +
                    "<p>Producer "+applicants.getProducerName()+ " will email you with any news about this event and your application</p>"
                    +"<p>Thank you for using our platform "+ homePage + "</p>"+
                    "<p>Feel free to use this URL "+ openJobs + " to explore more possibilities for you and your career</p>"+
                    "</body></html>";

            String htmlContentToProducer = "<html><body>" +
                    "<p>Hi " + producer + ",</p>" +
                    "<p>You have a new applicant applying for the role of "+roleApplied+". </p>" +
                    "<p>Please see message sent to you below by the applicant and login to view uploaded files </p>"
                    +"<p><b>"+message+"</b></p>"+
                    "</body></html>";

            try {
                SendMail(checkApplicant.getEmail(), "Application Notification", htmlContent);
                SendMail(userCheck.getEmail(), "Application Notification", htmlContentToProducer);
            }
            catch(Exception e) {
                log.info("Exception experienced is "+e.getMessage());
            }

            redirectAttributes.addFlashAttribute("status", "Records Saved Successfully. Please login");
            return "redirect:/home";

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("status", "Failed to save records, try again");
            return "redirect:/signup/"+producer;
        }

    }


}
