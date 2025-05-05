-- Script SQL pour la création des tables de la base de données consommationDeau (PostgreSQL)

-- Supprime les tables existantes si elles existent (utile pour le développement)
DROP TABLE IF EXISTS consommation CASCADE;
DROP TABLE IF EXISTS categorie CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

-- Table utilisateur
CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table categorie
CREATE TABLE categorie (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    utilisateur_id INT NOT NULL,
    CONSTRAINT fk_utilisateur_categorie FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Table consommation
CREATE TABLE consommation (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite >= 0), -- Quantité en litres
    description TEXT,
    utilisateur_id INT NOT NULL,
    categorie_id INT, -- Peut être NULL si non catégorisé
    date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_utilisateur_consommation FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_categorie_consommation FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE SET NULL
);

-- Index pour améliorer les performances des requêtes courantes
CREATE INDEX idx_consommation_utilisateur_date ON consommation (utilisateur_id, date);
CREATE INDEX idx_categorie_utilisateur ON categorie (utilisateur_id);

-- Quelques données initiales pour le test (optionnel)
-- INSERT INTO utilisateur (nom, email, mot_de_passe_hash) VALUES ('Test User', 'test@example.com', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08'); -- hash de 'test'
-- INSERT INTO categorie (nom, utilisateur_id) VALUES ('Boisson', 1);
-- INSERT INTO consommation (date, quantite, description, utilisateur_id, categorie_id) VALUES ('2025-05-04', 2.5, 'Eau du robinet', 1, 1);


