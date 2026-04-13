package appli.util;

import java.util.regex.Pattern;

/**
 * Utilitaires de validation de donnees en entree.
 * Fournit des methodes statiques pour valider les emails, telephones,
 * numeros de securite sociale, mots de passe, et pour nettoyer les chaines
 * (echappement HTML basique contre les injections XSS).
 */
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

    /** Retourne {@code true} si l'email est non vide et correspond au format standard. */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /** Retourne {@code true} si le numero de telephone est un numero francais valide (fixe ou mobile). */
    public static boolean isValidTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        return TELEPHONE_PATTERN.matcher(telephone.trim()).matches();
    }

    /** Retourne {@code true} si le numero de securite sociale est valide (format NIR francais 15 chiffres). */
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

    /**
     * Valide la force d'un mot de passe.
     * @return message d'erreur ou null si le mot de passe est valide
     */
    public static String validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caracteres";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Le mot de passe doit contenir au moins une majuscule";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Le mot de passe doit contenir au moins une minuscule";
        }
        if (!password.matches(".*\\d.*")) {
            return "Le mot de passe doit contenir au moins un chiffre";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "Le mot de passe doit contenir au moins un caractere special";
        }
        return null;
    }

    /**
     * Echappe les caracteres HTML dangereux pour prevenir les injections XSS.
     * Remplace {@code < > " '} par leurs entites HTML.
     */
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
