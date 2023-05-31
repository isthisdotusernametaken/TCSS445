package util;

import controller.ProgramDirectoryManager;

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

    private static final int ITERATIONS = 210_000; // As per OWASP recommendation 2023
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int HASH_SIZE = 64;
    private static final int BIT_KEY_LENGTH = HASH_SIZE * 8;

    public static void initialize() throws NoSuchAlgorithmException {
        KEY_FACTORY = SecretKeyFactory.getInstance(ALGORITHM);
    }

    public static boolean isValid(final String password) {
        return password != null && password.matches(REGEX);
    }

    // hashAndSalt[0] will return the hash, and hashAndSalt[1] will return the salt
    public static boolean saltAndHash(final String password, final byte[][] hashAndSalt) {
        hashAndSalt[1] = new byte[HASH_SIZE];
        RANDOM.nextBytes(hashAndSalt[1]);

        return hash(password, hashAndSalt);
    }

    // Pass the salt in hashAndSalt[1].
    // The hash will be returned in hashAndSalt[0].
    //
    // Because params already validated at initialization, exception should not
    // be encountered unless memory corrupted, library code fails to support
    // algorithm it claims (via Security and Providers) to support, or client
    // code fails to call isValid before attempting to hash
    public static boolean hash(final String password, final byte[][] hashAndSalt) {
        try {
            hashAndSalt[0] = KEY_FACTORY.generateSecret(
                    new PBEKeySpec(
                            password.toCharArray(),
                            hashAndSalt[1],
                            ITERATIONS,
                            BIT_KEY_LENGTH
                    )
            ).getEncoded();

            return true; // Successfully hashed, now check equality with old hash if validating
        } catch (NullPointerException | IllegalArgumentException e) {
            ProgramDirectoryManager.logError(e, "Invalid hash input", true);
            return false;
        } catch (InvalidKeySpecException e) {
            ProgramDirectoryManager.logError(e, "Hash algorithm/parameter failure", false);
            return false; // Never reached, unrecoverable error
        }
    }
}
