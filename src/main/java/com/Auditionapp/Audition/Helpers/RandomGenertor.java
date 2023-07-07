package com.Auditionapp.Audition.Helpers;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;



public class RandomGenertor {

    static final String NO = "0123456789";
    static SecureRandom rnd = new SecureRandom();


    public static String generateRandomString(String prefix) {
        String randomUUID = UUID.randomUUID().toString();
        String randomString = prefix + "_" + randomUUID.replaceAll("-", "");
        return randomString;
    }

    public static String generateNumericRef(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(NO.charAt(rnd.nextInt(NO.length())));
        }
        return sb.toString();
    }

    public static String dateConverter(String inputDate) {


        LocalDate date = LocalDate.parse(inputDate);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH);
        String formattedDate = date.format(outputFormatter).toUpperCase();

        return formattedDate;

    }


}

