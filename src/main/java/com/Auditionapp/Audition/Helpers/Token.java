package com.Auditionapp.Audition.Helpers;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class Token {

    public static String generateToken(){
        byte[] randomBytes = new byte[6];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return token.substring(0, 6);
    }

}
