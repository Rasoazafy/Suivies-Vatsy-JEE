## Liste des Tâches pour le Projet consommationDeau

1.  **[ ] Initialisation et Structure du Projet :**
    *   [X] Créer la structure de répertoires standard Maven pour une application web Java EE (`src/main/java`, `src/main/webapp`, etc.).
    *   [X] Configurer le fichier `pom.xml` avec les dépendances nécessaires (Jakarta EE API, Hibernate (JPA), Servlet API, JSP API, JSTL, Driver PostgreSQL, Bootstrap, Chart.js).
    *   [X] Configurer le fichier `web.xml` (si nécessaire pour la version de Servlet API utilisée).
    *   [X] Configurer le fichier `persistence.xml` pour JPA/Hibernate.

2.  **[ ] Base de Données :**
    *   [X] Définir le schéma de la base de données (tables : `utilisateur`, `consommation`, `categorie`).
    *   [X] Créer le script SQL (`schema.sql`) pour la création des tables et éventuellement quelques données initiales.

3.  **[ ] Backend - Modèle (JPA Entities) :**
    *   [X] Créer l'entité JPA `Utilisateur` (id, nom, email, mot_de_passe_hash).
    *   [X] Créer l'entité JPA `Categorie` (id, nom, utilisateur_id).
    *   [X] Créer l\'entité JPA `Consommation` (id, date, quantite, description, utilisateur_id, categorie_id).    *   [X] Définir les relations entre les entités (ex: OneToMany entre Utilisateur et Consommation/Categorie).

4.  **[ ] Backend - DAO (Data Access Objects) :**
    *   [X] Implémenter un DAO générique ou des DAOs spécifiques pour `Utilisateur`, `Categorie`, `Consommation`.
    *   [X] Implémenter les méthodes CRUD (Create, Read, Update, Delete) pour chaque entité en utilisant JPA.
    *   [X] Implémenter les méthodes spécifiques (ex: trouver utilisateur par email, trouver consommations par utilisateur et période, etc.).

5.  **[ ] Backend - Services (Logique Métier) :**
    *   [X] Service d\'authentification : `login(email, password)`, `register(nom, email, password)` (avec hachage SHA-256    *   [X] Service de gestion des catégories : CRUD pour les catégories propres à l\\'utilisateur.    *   [X] Service de gestion des consommations : CRUD pour les consommations, lié à l\\'utilisateur connecté.    *   [X] Service de calcul : `calculerTotalMensuel(utilisateurId, mois, annee)`, `verifierDepassementJournalier(utilisateurId, date)`.
    *   [X] Service de préparation des données pour le graphique.

6.  **[ ] Backend - Contrôleurs (Servlets) :**
    *   [X] Servlet pour l\\'authentification (login/logout, inscription)    *   [X] Servlet pour le tableau de bord (affichage principal).
    *   [X] Servlet pour la gestion CRUD des consommations.
    *   [X] Servlet pour la gestion CRUD des catégories.
    *   [X] Mettre en place des filtres pour la gestion de session et la protection des accès.

7.  **[ ] Frontend - Vues (JSP) :**
    *   [X] Créer la page de connexion (`login.jsp`).
    *   [X] Créer la page d\'inscription (`register.jsp`).    *   [X] Créer le tableau de bord (`dashboard.jsp`) : affichage liste consommations, total mensuel, alerte (>150L), graphique.
    *   [X] Créer le formulaire d\\'ajout/modification de consommation (`consommation_form.jsp`)    *   [X] Créer la page de gestion des catégories (`categories.jsp`).
    *   [X] Utiliser JSTL et Expression Language (EL).
    *   [X] Intégrer Bootstrap pour le style et le responsive design (en français).
    *   [X] Intégrer Chart.js pour le graphique (courbe, vue mensuelle par défaut).
    *   [X] Afficher les messages d'erreur/succès.    *   [X] Créer une page d\\'erreur générique.
8.  **[ ] Configuration Serveur et Déploiement :**
    *   [X] Préparer les instructions pour configurer la DataSource PostgreSQL dans WildFly.    *   [X] S\'assurer de la compatibilité avec WildFly (dernière version stable).
    *   [X] Configurer Maven pour générer le fichier `.war` (`consommationDeau.war`).

9.  **[ ] Documentation et Packaging :**
    *   [X] Rédiger le fichier `README.md` (ou `manuel_installation.txt`) avec instructions claires pour :
        *   [X] Création de la base PostgreSQL et exécution de `schema.sql`.
        *   [X] Configuration de la DataSource WildFly.
        *   [X] Déploiement du `.war`.
    *   [ ] Créer l'archive `.zip` finale contenant :
        *   [ ] Le code source complet du projet Maven.
        *   [ ] Le script `schema.sql`.
        *   [ ] Le fichier `README.md`.
        *   [ ] Le fichier `consommationDeau.war`.

10. **[ ] Validation Finale :**
    *   [ ] Vérifier que toutes les fonctionnalités demandées sont implémentées et fonctionnelles.
    *   [ ] Tester l'application (authentification, CRUDs, calculs, alerte, graphique).
    *   [ ] Vérifier la clarté et l'exactitude de la documentation.
    *   [ ] Vérifier le contenu de l'archive `.zip`.
