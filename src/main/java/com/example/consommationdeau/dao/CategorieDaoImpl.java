package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CategorieDaoImpl extends GenericDaoImpl<Categorie, Long> implements CategorieDao {
    public CategorieDaoImpl() {
        super(Categorie.class); // Pass the entity class
    }

    @Override
    public List<Categorie> findByUtilisateur(Utilisateur utilisateur) {
        return findByUtilisateurId(utilisateur.getId());
    }

    @Override
    public List<Categorie> findByUtilisateurId(Long utilisateurId) {
        TypedQuery<Categorie> query = entityManager.createQuery(
                "SELECT c FROM Categorie c WHERE c.utilisateur.id = :utilisateurId ORDER BY c.nom", Categorie.class);
        query.setParameter("utilisateurId", utilisateurId);
        return query.getResultList();
    }
}

