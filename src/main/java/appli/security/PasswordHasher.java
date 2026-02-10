package appli.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire de hachage de mots de passe avec BCrypt.
 * Cost factor 12 : bon compromis securite/performance.
 */
public class PasswordHasher {

    private static final int BCRYPT_COST = 12;

    private PasswordHasher() {}

    /**
     * Hash un mot de passe en clair avec BCrypt.
     * @param password mot de passe en clair
     * @return hash BCrypt (contient le salt integre)
     */
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verifie un mot de passe en clair contre un hash BCrypt stocke.
     * @param password mot de passe en clair
     * @param storedHash hash BCrypt stocke en base
     * @return true si le mot de passe correspond
     */
    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (IllegalArgumentException e) {
            // Hash invalide (format non-BCrypt)
            return false;
        }
    }
}
