package com.klimalakamil.iot_platform.core.authentication;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 15.01.17.
 */
public class PasswordHelper {

    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 7500;
    private static final int KEY_LENGTH = 256;

    private static final Logger logger = Logger.getLogger(PasswordHelper.class.getName());

    private PasswordHelper() {
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[8];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] createHash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
        return null;
    }

    public static boolean checkPassword(char[] password, byte[] salt, byte[] hash) {
        byte[] passwordHash = createHash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        return compareBytes(hash, passwordHash);
    }

    private static boolean compareBytes(byte[] a, byte[] b) {
        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i])
                return false;
        }

        return true;
    }
}