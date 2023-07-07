package com.Auditionapp.Audition.Controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/applicants")
@Slf4j
public class ApplicantsController {


    @Value("${signUpPage}")
    private String signUpPage;

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
}
