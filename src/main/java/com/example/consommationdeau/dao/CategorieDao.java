package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Utilisateur;
import java.util.List;

/**
 * Interface DAO spécifique pour l'entité Categorie.
 */
public interface CategorieDao extends GenericDao<Categorie, Long> {

    /**
     * Trouve toutes les catégories appartenant à un utilisateur spécifique.
     *
     * @param utilisateur L'utilisateur dont on cherche les catégories.
     * @return Une liste des catégories de l'utilisateur.
     */
    List<Categorie> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve toutes les catégories appartenant à un utilisateur spécifique par son ID.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @return Une liste des catégories de l'utilisateur.
     */
    List<Categorie> findByUtilisateurId(Long utilisateurId);

}

