package com.example.consommationdeau.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Utilitaire pour le hachage des mots de passe.
 */
public class PasswordUtil {

    /**
     * Hache un mot de passe en utilisant SHA-256.
     *
     * @param password Le mot de passe en clair.
     * @return Le hash SHA-256 du mot de passe.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        return DigestUtils.sha256Hex(password);
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash.
     *
     * @param plainPassword Le mot de passe en clair.
     * @param hashedPassword Le hash stocké.
     * @return true si le mot de passe correspond au hash, false sinon.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String hashOfPlainPassword = hashPassword(plainPassword);
        return hashedPassword.equals(hashOfPlainPassword);
    }
}

