package appli.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern TELEPHONE_PATTERN = Pattern.compile(
            "^(0|\\+33)[1-9]([-. ]?[0-9]{2}){4}$"
    );

    private static final Pattern NUMERO_SECU_PATTERN = Pattern.compile(
            "^[12][0-9]{2}(0[1-9]|1[0-2])[0-9]{2}[0-9]{3}[0-9]{3}[0-9]{2}$"
    );

    private ValidationUtils() {}

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        return TELEPHONE_PATTERN.matcher(telephone.trim()).matches();
    }

    public static boolean isValidNumeroSecuriteSociale(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        String cleaned = numero.replaceAll("\\s", "");
        return NUMERO_SECU_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isNotNull(Object value) {
        return value != null;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isPositiveOrZero(int value) {
        return value >= 0;
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
