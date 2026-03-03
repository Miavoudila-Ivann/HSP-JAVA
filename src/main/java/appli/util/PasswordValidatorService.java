package appli.util;

public class PasswordValidatorService {

    public PasswordValidationResult validate(String password) {

        PasswordValidationResult result = new PasswordValidationResult();

        if (password == null || password.isEmpty()) {
            result.addError("Le mot de passe ne doit pas être vide.");
            return result;
        }

        if (password.length() < 8) {
            result.addError("Le mot de passe doit contenir au moins 8 caractères.");
        }

        if (!password.matches(".*[A-Z].*")) {
            result.addError("Le mot de passe doit contenir au moins une majuscule.");
        }

        if (!password.matches(".*[a-z].*")) {
            result.addError("Le mot de passe doit contenir au moins une minuscule.");
        }

        if (!password.matches(".*\\d.*")) {
            result.addError("Le mot de passe doit contenir au moins un chiffre.");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            result.addError("Le mot de passe doit contenir au moins un caractère spécial.");
        }

        return result;
    }
}