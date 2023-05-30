package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Password {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 30;
    private static final String SPECIAL_CHARACTERS = "~`!@#$%^&*()_=+-"; // Always in []
    // 10-30 characters, which must and can only include 1+ lowercase
    // letters, 1+ uppercase letters, 1+ decimal digits, and 1+ special
    // characters
    private static final String REGEX =
            "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[" + SPECIAL_CHARACTERS + "])" +
            "[a-zA-Z\\d" + SPECIAL_CHARACTERS + "]{" + MIN_LENGTH + ',' + MAX_LENGTH + "}";


    private static final SecureRandom RANDOM = new SecureRandom();
    private static SecretKeyFactory KEY_FACTORY;

    private static final int ITERATIONS = 1024 * 1024;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int HASH_SIZE = 64;
    private static final int BIT_KEY_LENGTH = HASH_SIZE * 8;

    public static void initialize() throws NoSuchAlgorithmException {
        KEY_FACTORY = SecretKeyFactory.getInstance(ALGORITHM);
    }

    public static boolean isValid(final String password) {
        return password != null && password.matches(REGEX);
    }

    public static byte[][] saltAndHash(final String password)
            throws InvalidKeySpecException, NullPointerException, IllegalArgumentException {
        final var salt = new byte[HASH_SIZE];
        RANDOM.nextBytes(salt);

        return new byte[][]{salt, hash(password, salt)};
    }

    // Because params already validated at initialization, exception should not
    // be encountered unless memory corrupted, library code fails to support
    // algorithm it claims (via Security and Providers) to support, or client
    // code fails to call isValid before attempting to hash
    public static byte[] hash(final String password, final byte[] salt)
            throws InvalidKeySpecException, NullPointerException, IllegalArgumentException {
        return KEY_FACTORY.generateSecret(
                new PBEKeySpec(
                        password.toCharArray(),
                        salt,
                        ITERATIONS,
                        BIT_KEY_LENGTH
                )
        ).getEncoded();
    }
}
