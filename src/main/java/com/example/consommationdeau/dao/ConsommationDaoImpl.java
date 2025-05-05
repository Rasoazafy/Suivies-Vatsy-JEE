package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class ConsommationDaoImpl extends GenericDaoImpl<Consommation, Long> implements ConsommationDao {
    public ConsommationDaoImpl() {
        super(Consommation.class); // Pass the entity class
    }

    @Override
    public List<Consommation> findByUtilisateur(Utilisateur utilisateur) {
        return findByUtilisateurId(utilisateur.getId());
    }

    @Override
    public List<Consommation> findByUtilisateurId(Long utilisateurId) {
        TypedQuery<Consommation> query = entityManager.createQuery(
                "SELECT c FROM Consommation c LEFT JOIN FETCH c.categorie WHERE c.utilisateur.id = :utilisateurId ORDER BY c.date DESC, c.id DESC", Consommation.class);
        query.setParameter("utilisateurId", utilisateurId);
        return query.getResultList();
    }

    @Override
    public List<Consommation> findByUtilisateurIdAndDateBetween(Long utilisateurId, Date dateDebut, Date dateFin) {
        TypedQuery<Consommation> query = entityManager.createQuery(
                "SELECT c FROM Consommation c LEFT JOIN FETCH c.categorie WHERE c.utilisateur.id = :utilisateurId AND c.date BETWEEN :dateDebut AND :dateFin ORDER BY c.date DESC, c.id DESC", Consommation.class);
        query.setParameter("utilisateurId", utilisateurId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    @Override
    public BigDecimal sumQuantiteByUtilisateurIdAndDate(Long utilisateurId, Date date) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT SUM(c.quantite) FROM Consommation c WHERE c.utilisateur.id = :utilisateurId AND c.date = :date", BigDecimal.class);
        query.setParameter("utilisateurId", utilisateurId);
        query.setParameter("date", date);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumQuantiteByUtilisateurIdAndMonth(Long utilisateurId, int annee, int mois) {
        YearMonth yearMonth = YearMonth.of(annee, mois);
        LocalDate debutMois = yearMonth.atDay(1);
        LocalDate finMois = yearMonth.atEndOfMonth();

        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT SUM(c.quantite) FROM Consommation c WHERE c.utilisateur.id = :utilisateurId AND c.date BETWEEN :dateDebut AND :dateFin", BigDecimal.class);
        query.setParameter("utilisateurId", utilisateurId);
        query.setParameter("dateDebut", Date.valueOf(debutMois));
        query.setParameter("dateFin", Date.valueOf(finMois));
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public Map<Date, BigDecimal> findDailyConsumptionSumForGraph(Long utilisateurId, Date dateDebut, Date dateFin) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT c.date, SUM(c.quantite) FROM Consommation c WHERE c.utilisateur.id = :utilisateurId AND c.date BETWEEN :dateDebut AND :dateFin GROUP BY c.date ORDER BY c.date ASC", Object[].class);
        query.setParameter("utilisateurId", utilisateurId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);

        List<Object[]> results = query.getResultList();

        // Convertir la liste de Object[] en Map<Date, BigDecimal>
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Date) result[0],
                        result -> (BigDecimal) result[1]
                ));
    }
}

