package com.example.consommationdeau.dao;

import com.example.consommationdeau.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

@Stateless // Marque ce DAO comme un EJB Stateless pour l'injection
public class UtilisateurDaoImpl extends GenericDaoImpl<Utilisateur, Long> implements UtilisateurDao {
    public UtilisateurDaoImpl() {
        super(Utilisateur.class); // Pass the entity class
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

