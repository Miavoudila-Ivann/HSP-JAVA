package appli.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    private static final String SEPARATOR = ":";

    private PasswordHasher() {}

    public static String hash(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = hashWithSalt(password, salt);

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + SEPARATOR + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    public static boolean verify(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash = hashWithSalt(password, salt);

            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        digest.update(salt);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
