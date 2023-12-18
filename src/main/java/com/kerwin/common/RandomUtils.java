package com.kerwin.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtils {

    private static final int DEF_COUNT = 20;

    private static final SecureRandom SECURE_RANDOM;

    static {
        SECURE_RANDOM = new SecureRandom();
        SECURE_RANDOM.nextBytes(new byte[64]);
    }

    private RandomUtils() {
    }

    public static String generateRandomAlphanumericString() {
        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);
    }

    public static String generateRandomString(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, SECURE_RANDOM);
    }

    public static String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a password.
     *
     * @return the generated password.
     */
    public static String generatePassword() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key.
     */
    public static String generateActivationKey() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key.
     */
    public static String generateResetKey() {
        return generateRandomAlphanumericString();
    }

}

