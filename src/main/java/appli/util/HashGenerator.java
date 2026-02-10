package appli.util;

import appli.security.PasswordHasher;

/**
 * Utilitaire pour generer des hashes BCrypt (pour seed.sql).
 * Usage : executer la methode main() pour obtenir le hash de "password123".
 */
public class HashGenerator {

    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "password123";
        String hash = PasswordHasher.hash(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println();
        System.out.println("Pour seed.sql, remplacer les password_hash par :");
        System.out.println("'" + hash + "'");
    }
}
