package appli.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Service TOTP (RFC 6238) — authentification a deux facteurs.
 * Compatible Google Authenticator, Authy, Microsoft Authenticator.
 */
public class TotpService {

    private static final int CODE_DIGITS = 6;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int WINDOW = 1; // tolerance ±1 intervalle (30s avant/apres)

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private TotpService() {}

    /** Genere une nouvelle cle secrete aleatoire (20 octets, encodee Base32). */
    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return base32Encode(bytes);
    }

    /**
     * Verifie un code OTP a 6 chiffres saisi par l'utilisateur.
     * Accepte les codes valides dans la fenetre [-WINDOW, +WINDOW] intervalles.
     */
    public static boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) return false;
        String trimmed = code.trim();
        if (trimmed.length() != CODE_DIGITS) return false;
        try {
            int inputCode = Integer.parseInt(trimmed);
            long timeStep = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
            for (int i = -WINDOW; i <= WINDOW; i++) {
                if (generateCode(secret, timeStep + i) == inputCode) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Retourne l'URI otpauth:// a encoder en QR code pour l'application
     * d'authentification (Google Authenticator, Authy...).
     */
    public static String getOtpAuthUri(String secret, String email) {
        return "otpauth://totp/HSP-Java:" + email
                + "?secret=" + secret
                + "&issuer=HSP-Java"
                + "&digits=" + CODE_DIGITS
                + "&period=" + TIME_STEP_SECONDS;
    }

    // --- Algorithme TOTP (HMAC-SHA1 + truncature dynamique) ---

    private static int generateCode(String secret, long timeStep) {
        try {
            byte[] key = base32Decode(secret);
            byte[] message = longToBytes(timeStep);

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(message);

            int offset = hash[hash.length - 1] & 0x0F;
            int truncated = ((hash[offset]     & 0x7F) << 24)
                          | ((hash[offset + 1] & 0xFF) << 16)
                          | ((hash[offset + 2] & 0xFF) << 8)
                          |  (hash[offset + 3] & 0xFF);

            return truncated % (int) Math.pow(10, CODE_DIGITS);
        } catch (Exception e) {
            throw new RuntimeException("Erreur generation TOTP", e);
        }
    }

    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    // --- Base32 encode/decode ---

    public static String base32Encode(byte[] input) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0, bitsLeft = 0;
        for (byte b : input) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                bitsLeft -= 5;
                sb.append(BASE32_CHARS.charAt((buffer >> bitsLeft) & 0x1F));
            }
        }
        if (bitsLeft > 0) {
            sb.append(BASE32_CHARS.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        }
        return sb.toString();
    }

    public static byte[] base32Decode(String input) {
        input = input.toUpperCase().replaceAll("[^A-Z2-7]", "");
        byte[] result = new byte[input.length() * 5 / 8];
        int buffer = 0, bitsLeft = 0, index = 0;
        for (char c : input.toCharArray()) {
            int value = BASE32_CHARS.indexOf(c);
            if (value < 0) continue;
            buffer = (buffer << 5) | value;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bitsLeft -= 8;
                if (index < result.length) {
                    result[index++] = (byte) ((buffer >> bitsLeft) & 0xFF);
                }
            }
        }
        return result;
    }
}
