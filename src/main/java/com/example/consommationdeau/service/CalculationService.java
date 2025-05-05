package com.example.consommationdeau.service;

import com.example.consommationdeau.dao.ConsommationDao;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/**
 * Service pour les calculs liés à la consommation d'eau.
 */
@Stateless
public class CalculationService {

    @Inject
    private ConsommationDao consommationDao;

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("150.00");

    /**
     * Calcule la consommation totale d'eau pour un utilisateur pour un mois donné.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param annee         L'année.
     * @param mois          Le mois (1-12).
     * @return La consommation totale pour le mois spécifié.
     */
    public BigDecimal calculerTotalMensuel(Long utilisateurId, int annee, int mois) {
        return consommationDao.sumQuantiteByUtilisateurIdAndMonth(utilisateurId, annee, mois);
    }

    /**
     * Vérifie si la consommation d'un utilisateur pour un jour donné dépasse la limite.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param date          La date à vérifier.
     * @return true si la limite est dépassée, false sinon.
     */
    public boolean verifierDepassementJournalier(Long utilisateurId, Date date) {
        BigDecimal consommationJournaliere = consommationDao.sumQuantiteByUtilisateurIdAndDate(utilisateurId, date);
        return consommationJournaliere.compareTo(DAILY_LIMIT) > 0;
    }

    /**
     * Récupère les données de consommation journalière pour un utilisateur sur une période donnée,
     * formatées pour un graphique.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param dateDebut     La date de début de la période.
     * @param dateFin       La date de fin de la période.
     * @return Une Map où la clé est la Date et la valeur est la somme des quantités pour ce jour.
     */
    public Map<Date, BigDecimal> preparerDonneesGraphique(Long utilisateurId, Date dateDebut, Date dateFin) {
        // Valider les dates si nécessaire (ex: dateDebut <= dateFin)
        if (dateDebut == null || dateFin == null || dateDebut.after(dateFin)) {
             // Retourner une map vide ou lever une exception selon la gestion d'erreur choisie
             // Pour l'instant, on utilise les dates du mois courant si les dates sont invalides
             YearMonth currentMonth = YearMonth.now();
             dateDebut = Date.valueOf(currentMonth.atDay(1));
             dateFin = Date.valueOf(currentMonth.atEndOfMonth());
        }
        return consommationDao.findDailyConsumptionSumForGraph(utilisateurId, dateDebut, dateFin);
    }

    /**
     * Récupère les données de consommation journalière pour le mois courant.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @return Une Map où la clé est la Date et la valeur est la somme des quantités pour ce jour.
     */
    public Map<Date, BigDecimal> preparerDonneesGraphiqueMoisCourant(Long utilisateurId) {
        YearMonth currentMonth = YearMonth.now();
        Date dateDebut = Date.valueOf(currentMonth.atDay(1));
        Date dateFin = Date.valueOf(currentMonth.atEndOfMonth());
        return preparerDonneesGraphique(utilisateurId, dateDebut, dateFin);
    }
}

