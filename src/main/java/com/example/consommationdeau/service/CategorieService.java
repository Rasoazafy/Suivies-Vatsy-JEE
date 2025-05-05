package com.example.consommationdeau.service;

import com.example.consommationdeau.dao.CategorieDao;
import com.example.consommationdeau.dao.UtilisateurDao;
import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des catégories.
 */
@Stateless
public class CategorieService {

    @Inject
    private CategorieDao categorieDao;

    @Inject
    private UtilisateurDao utilisateurDao; // Injecter si besoin de récupérer l'utilisateur

    /**
     * Crée une nouvelle catégorie pour un utilisateur donné.
     *
     * @param nom           Le nom de la catégorie.
     * @param utilisateurId L'ID de l'utilisateur propriétaire.
     * @return La catégorie créée.
     * @throws IllegalArgumentException si l'utilisateur n'est pas trouvé.
     */
    public Categorie creerCategorie(String nom, Long utilisateurId) throws IllegalArgumentException {
        Utilisateur utilisateur = utilisateurDao.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        // Vérifier si une catégorie avec le même nom existe déjà pour cet utilisateur ? (Optionnel)
        // List<Categorie> existingCategories = categorieDao.findByUtilisateurId(utilisateurId);
        // boolean nameExists = existingCategories.stream().anyMatch(cat -> cat.getNom().equalsIgnoreCase(nom));
        // if (nameExists) {
        //     throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà.");
        // }

        Categorie nouvelleCategorie = new Categorie(nom, utilisateur);
        return categorieDao.save(nouvelleCategorie);
    }

    /**
     * Récupère toutes les catégories d'un utilisateur.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @return La liste des catégories de l'utilisateur.
     */
    public List<Categorie> listerCategoriesParUtilisateur(Long utilisateurId) {
        return categorieDao.findByUtilisateurId(utilisateurId);
    }

    /**
     * Trouve une catégorie par son ID.
     *
     * @param id L'ID de la catégorie.
     * @return Un Optional contenant la catégorie si trouvée.
     */
    public Optional<Categorie> trouverCategorieParId(Long id) {
        return categorieDao.findById(id);
    }

    /**
     * Met à jour une catégorie existante.
     *
     * @param id            L'ID de la catégorie à mettre à jour.
     * @param nom           Le nouveau nom de la catégorie.
     * @param utilisateurId L'ID de l'utilisateur propriétaire (pour vérification).
     * @return La catégorie mise à jour.
     * @throws IllegalArgumentException si la catégorie n'est pas trouvée ou n'appartient pas à l'utilisateur.
     */
    public Categorie mettreAJourCategorie(Long id, String nom, Long utilisateurId) throws IllegalArgumentException {
        Categorie categorie = categorieDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + id));

        // Vérifier que la catégorie appartient bien à l'utilisateur
        if (!categorie.getUtilisateur().getId().equals(utilisateurId)) {
            throw new IllegalArgumentException("Cette catégorie n'appartient pas à l'utilisateur spécifié.");
        }

        // Vérifier si le nouveau nom existe déjà pour cet utilisateur (Optionnel, sauf si c'est le même ID)
        // List<Categorie> existingCategories = categorieDao.findByUtilisateurId(utilisateurId);
        // boolean nameExists = existingCategories.stream()
        //        .anyMatch(cat -> cat.getNom().equalsIgnoreCase(nom) && !cat.getId().equals(id));
        // if (nameExists) {
        //     throw new IllegalArgumentException("Une autre catégorie avec ce nom existe déjà.");
        // }

        categorie.setNom(nom);
        return categorieDao.update(categorie);
    }

    /**
     * Supprime une catégorie.
     * Note : La suppression peut échouer ou avoir des effets de bord si des consommations y sont liées,
     * en fonction de la configuration de la base de données (ON DELETE SET NULL ici).
     *
     * @param id            L'ID de la catégorie à supprimer.
     * @param utilisateurId L'ID de l'utilisateur propriétaire (pour vérification).
     * @throws IllegalArgumentException si la catégorie n'est pas trouvée ou n'appartient pas à l'utilisateur.
     */
    public void supprimerCategorie(Long id, Long utilisateurId) throws IllegalArgumentException {
        Categorie categorie = categorieDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + id));

        // Vérifier que la catégorie appartient bien à l'utilisateur
        if (!categorie.getUtilisateur().getId().equals(utilisateurId)) {
            throw new IllegalArgumentException("Cette catégorie n'appartient pas à l'utilisateur spécifié.");
        }

        categorieDao.delete(categorie);
    }
}

