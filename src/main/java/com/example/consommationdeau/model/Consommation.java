package com.example.consommationdeau.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "consommation")
public class Consommation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "quantite", precision = 10, scale = 2)
    private BigDecimal quantite;


    @Column(name = "description") // Pour les descriptions potentiellement longues
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id") // nullable = true par défaut, FK contrainte gère SET NULL
    private Categorie categorie;

    @Column(name = "date_enregistrement")
    private Timestamp dateEnregistrement; // Changer Date en Timestamp


    // Constructeurs
    public Consommation() {
    }

    public Consommation(Date date, BigDecimal quantite, String description, Utilisateur utilisateur, Categorie categorie) {
        this.date = date;
        this.quantite = quantite;
        this.description = description;
        this.utilisateur = utilisateur;
        this.categorie = categorie;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Timestamp getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(Timestamp dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consommation that = (Consommation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Consommation{" +
               "id=" + id +
               ", date=" + date +
               ", quantite=" + quantite +
               ", description=\"" + description + "\"" +
               // Éviter de charger paresseusement dans toString
               ", utilisateurId=" + (utilisateur != null ? utilisateur.getId() : null) +
               ", categorieId=" + (categorie != null ? categorie.getId() : null) +
               ", dateEnregistrement=" + dateEnregistrement +
               '}';
    }
}

