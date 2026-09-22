package com.project.razorpay.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Generates random secret Base64 string of given length.
    public static String randomBase64(int length) {

        byte[] buf = new byte[length];
        // Generate random bytes and encode them to Base64
        SECURE_RANDOM.nextBytes(buf);
        // [4, 12, 100, -12] {-128, 127}
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
