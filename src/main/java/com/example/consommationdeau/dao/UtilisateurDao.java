package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Utilisateur;
import java.util.Optional;

/**
 * Interface DAO spécifique pour l'entité Utilisateur.
 */
public interface UtilisateurDao extends GenericDao<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son adresse email.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return Un Optional contenant l'utilisateur si trouvé, sinon Optional vide.
     */
    Optional<Utilisateur> findByEmail(String email);

}

