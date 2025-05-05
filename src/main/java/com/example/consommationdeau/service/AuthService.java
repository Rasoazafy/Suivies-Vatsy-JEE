package com.example.consommationdeau.service;

import com.example.consommationdeau.dao.UtilisateurDao;
import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Optional;

/**
 * Service pour l'authentification et la gestion des utilisateurs.
 */
@Stateless
public class AuthService {

    @Inject
    private UtilisateurDao utilisateurDao;

    /**
     * Tente de connecter un utilisateur.
     *
     * @param email    L'email de l'utilisateur.
     * @param password Le mot de passe en clair.
     * @return Un Optional contenant l'Utilisateur si l'authentification réussit, sinon Optional vide.
     */
    public Optional<Utilisateur> login(String email, String password) {
        Optional<Utilisateur> userOpt = utilisateurDao.findByEmail(email);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            if (PasswordUtil.checkPassword(password, user.getMotDePasseHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param nom      Le nom de l'utilisateur.
     * @param email    L'email de l'utilisateur (doit être unique).
     * @param password Le mot de passe en clair.
     * @return L'utilisateur créé.
     * @throws IllegalArgumentException si l'email est déjà utilisé.
     */
    public Utilisateur register(String nom, String email, String password) throws IllegalArgumentException {
        // Vérifier si l'email existe déjà
        if (utilisateurDao.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("L\'adresse email est déjà utilisée.");
        }

        // Hacher le mot de passe
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Créer le nouvel utilisateur
        Utilisateur newUser = new Utilisateur(nom, email, hashedPassword);

        // Sauvegarder l'utilisateur
        return utilisateurDao.save(newUser);
    }

     /**
     * Trouve un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur.
     * @return Un Optional contenant l'utilisateur si trouvé.
     */
    public Optional<Utilisateur> findUserById(Long id) {
        return utilisateurDao.findById(id);
    }
}

