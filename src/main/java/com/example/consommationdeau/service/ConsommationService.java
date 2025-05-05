package com.example.consommationdeau.service;

import com.example.consommationdeau.dao.CategorieDao;
import com.example.consommationdeau.dao.ConsommationDao;
import com.example.consommationdeau.dao.UtilisateurDao;
import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des consommations d'eau.
 */
@Stateless
public class ConsommationService {

    @Inject
    private ConsommationDao consommationDao;

    @Inject
    private UtilisateurDao utilisateurDao;

    @Inject
    private CategorieDao categorieDao;

    /**
     * Enregistre une nouvelle consommation pour un utilisateur.
     *
     * @param date          La date de la consommation.
     * @param quantite      La quantité consommée (en litres).
     * @param description   Une description optionnelle.
     * @param utilisateurId L'ID de l'utilisateur.
     * @param categorieId   L'ID de la catégorie (peut être null).
     * @return La consommation enregistrée.
     * @throws IllegalArgumentException si l'utilisateur ou la catégorie (si fournie) n'est pas trouvé.
     */
    public Consommation enregistrerConsommation(Date date, BigDecimal quantite, String description, Long utilisateurId, Long categorieId)
            throws IllegalArgumentException {

        Utilisateur utilisateur = utilisateurDao.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        Categorie categorie = null;
        if (categorieId != null) {
            categorie = categorieDao.findById(categorieId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + categorieId));
            // Vérifier que la catégorie appartient bien à l'utilisateur
            if (!categorie.getUtilisateur().getId().equals(utilisateurId)) {
                throw new IllegalArgumentException("La catégorie spécifiée n'appartient pas à cet utilisateur.");
            }
        }

        // Validation de la quantité (positive)
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La quantité doit être un nombre positif.");
        }
        // Validation de la date (non nulle)
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être nulle.");
        }

        Consommation nouvelleConsommation = new Consommation(date, quantite, description, utilisateur, categorie);
        return consommationDao.save(nouvelleConsommation);
    }

    /**
     * Récupère toutes les consommations d'un utilisateur.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @return La liste des consommations triée par date décroissante.
     */
    public List<Consommation> listerConsommationsParUtilisateur(Long utilisateurId) {
        return consommationDao.findByUtilisateurId(utilisateurId);
    }

    /**
     * Trouve une consommation par son ID.
     *
     * @param id L'ID de la consommation.
     * @return Un Optional contenant la consommation si trouvée.
     */
    public Optional<Consommation> trouverConsommationParId(Long id) {
        return consommationDao.findById(id);
    }

    /**
     * Met à jour une consommation existante.
     *
     * @param id            L'ID de la consommation à mettre à jour.
     * @param date          La nouvelle date.
     * @param quantite      La nouvelle quantité.
     * @param description   La nouvelle description.
     * @param utilisateurId L'ID de l'utilisateur propriétaire (pour vérification).
     * @param categorieId   Le nouvel ID de catégorie (peut être null).
     * @return La consommation mise à jour.
     * @throws IllegalArgumentException si la consommation, l'utilisateur ou la catégorie n'est pas trouvé, ou si la consommation n'appartient pas à l'utilisateur.
     */
    public Consommation mettreAJourConsommation(Long id, Date date, BigDecimal quantite, String description, Long utilisateurId, Long categorieId)
            throws IllegalArgumentException {

        Consommation consommation = consommationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consommation non trouvée avec l'ID: " + id));

        // Vérifier que la consommation appartient bien à l'utilisateur
        if (!consommation.getUtilisateur().getId().equals(utilisateurId)) {
            throw new IllegalArgumentException("Cette consommation n'appartient pas à l'utilisateur spécifié.");
        }

        Categorie categorie = null;
        if (categorieId != null) {
            categorie = categorieDao.findById(categorieId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + categorieId));
            // Vérifier que la catégorie appartient bien à l'utilisateur
            if (!categorie.getUtilisateur().getId().equals(utilisateurId)) {
                throw new IllegalArgumentException("La catégorie spécifiée n'appartient pas à cet utilisateur.");
            }
        }

        // Validation
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La quantité doit être un nombre positif.");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être nulle.");
        }

        consommation.setDate(date);
        consommation.setQuantite(quantite);
        consommation.setDescription(description);
        consommation.setCategorie(categorie); // Met à jour ou met à null si categorieId est null

        return consommationDao.update(consommation);
    }

    /**
     * Supprime une consommation.
     *
     * @param id            L'ID de la consommation à supprimer.
     * @param utilisateurId L'ID de l'utilisateur propriétaire (pour vérification).
     * @throws IllegalArgumentException si la consommation n'est pas trouvée ou n'appartient pas à l'utilisateur.
     */
    public void supprimerConsommation(Long id, Long utilisateurId) throws IllegalArgumentException {
        Consommation consommation = consommationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consommation non trouvée avec l'ID: " + id));

        // Vérifier que la consommation appartient bien à l'utilisateur
        if (!consommation.getUtilisateur().getId().equals(utilisateurId)) {
            throw new IllegalArgumentException("Cette consommation n'appartient pas à l'utilisateur spécifié.");
        }

        consommationDao.delete(consommation);
    }
}

