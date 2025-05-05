package com.example.consommationdeau.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO générique pour les opérations CRUD de base.
 *
 * @param <T>  Le type de l'entité
 * @param <ID> Le type de l'identifiant de l'entité (généralement Long ou Integer)
 */
public interface GenericDao<T, ID extends Serializable> {

    /**
     * Trouve une entité par son identifiant.
     *
     * @param id L'identifiant de l'entité.
     * @return Un Optional contenant l'entité si trouvée, sinon Optional vide.
     */
    Optional<T> findById(ID id);

    /**
     * Récupère toutes les entités d'un certain type.
     *
     * @return Une liste de toutes les entités.
     */
    List<T> findAll();

    /**
     * Sauvegarde (crée ou met à jour) une entité.
     *
     * @param entity L'entité à sauvegarder.
     * @return L'entité sauvegardée (peut être différente si gérée par JPA).
     */
    T save(T entity);

    /**
     * Met à jour une entité existante.
     *
     * @param entity L'entité à mettre à jour.
     * @return L'entité mise à jour.
     */
    T update(T entity);

    /**
     * Supprime une entité.
     *
     * @param entity L'entité à supprimer.
     */
    void delete(T entity);

    /**
     * Supprime une entité par son identifiant.
     *
     * @param id L'identifiant de l'entité à supprimer.
     */
    void deleteById(ID id);

    /**
     * Compte le nombre total d'entités.
     *
     * @return Le nombre total d'entités.
     */
    long count();
}

