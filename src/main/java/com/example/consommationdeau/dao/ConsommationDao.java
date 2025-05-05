package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface DAO spécifique pour l'entité Consommation.
 */
public interface ConsommationDao extends GenericDao<Consommation, Long> {

    /**
     * Trouve toutes les consommations d'un utilisateur spécifique.
     *
     * @param utilisateur L'utilisateur dont on cherche les consommations.
     * @return Une liste des consommations de l'utilisateur, triées par date (par exemple).
     */
    List<Consommation> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve toutes les consommations d'un utilisateur spécifique par son ID.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @return Une liste des consommations de l'utilisateur.
     */
    List<Consommation> findByUtilisateurId(Long utilisateurId);

    /**
     * Trouve les consommations d'un utilisateur pour une période donnée.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param dateDebut     La date de début de la période.
     * @param dateFin       La date de fin de la période.
     * @return Une liste des consommations dans la période spécifiée.
     */
    List<Consommation> findByUtilisateurIdAndDateBetween(Long utilisateurId, Date dateDebut, Date dateFin);

    /**
     * Calcule la somme des quantités consommées par un utilisateur pour un jour donné.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param date          Le jour concerné.
     * @return La somme des quantités pour ce jour, ou BigDecimal.ZERO si aucune consommation.
     */
    BigDecimal sumQuantiteByUtilisateurIdAndDate(Long utilisateurId, Date date);

    /**
     * Calcule la somme des quantités consommées par un utilisateur pour un mois donné.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param annee         L'année du mois concerné.
     * @param mois          Le mois concerné (1-12).
     * @return La somme des quantités pour ce mois, ou BigDecimal.ZERO si aucune consommation.
     */
    BigDecimal sumQuantiteByUtilisateurIdAndMonth(Long utilisateurId, int annee, int mois);

    /**
     * Récupère les données de consommation agrégées par jour pour un utilisateur et une période,
     * pour être utilisées dans un graphique.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param dateDebut     La date de début de la période.
     * @param dateFin       La date de fin de la période.
     * @return Une Map où la clé est la Date et la valeur est la somme des quantités pour ce jour.
     */
    Map<Date, BigDecimal> findDailyConsumptionSumForGraph(Long utilisateurId, Date dateDebut, Date dateFin);

}

