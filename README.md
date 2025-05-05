# Projet Suivi Consommation d'Eau (consommationDeau)

Application web Java EE (Jakarta EE) pour suivre et gérer sa consommation d'eau quotidienne.

## Fonctionnalités

*   Authentification utilisateur (Inscription / Connexion / Déconnexion).
*   Gestion CRUD complète des enregistrements de consommation d'eau (date, quantité, description, catégorie).
*   Gestion CRUD des catégories de consommation propres à chaque utilisateur.
*   Calcul du total de consommation pour le mois en cours.
*   Affichage d'une alerte sur le tableau de bord si la consommation journalière dépasse 150 litres.
*   Graphique (courbe) montrant l'évolution de la consommation journalière pour le mois en cours.
*   Interface utilisateur responsive basée sur Bootstrap 5.

## Technologies Utilisées

*   **Langage :** Java 11
*   **Frameworks/API :** Jakarta EE 10 (Servlet 6.0, JSP 3.1, JPA 3.1, EJB Lite 4.0, CDI 4.0)
*   **ORM :** Hibernate 6.4
*   **Base de données :** PostgreSQL (testé avec PostgreSQL 14+)
*   **Serveur d'application :** WildFly 34+ (testé avec WildFly 34.0.1.Final)
*   **Build Tool :** Apache Maven 3.8+
*   **Frontend :** JSP, JSTL, Bootstrap 5.3, Chart.js 4.4
*   **Utilitaires :** Apache Commons Codec (pour SHA-256)

## Instructions d'Installation et de Déploiement

### Prérequis

1.  **Java Development Kit (JDK) :** Version 11 ou supérieure installée.
2.  **Apache Maven :** Version 3.8 ou supérieure installée.
3.  **PostgreSQL :** Serveur PostgreSQL installé et en cours d'exécution.
4.  **WildFly :** Serveur d'application WildFly (version 34 ou supérieure) téléchargé et décompressé.

### 1. Création de la Base de Données PostgreSQL

1.  Connectez-vous à votre serveur PostgreSQL (par exemple, en utilisant `psql` ou un outil graphique comme pgAdmin).
2.  Créez une nouvelle base de données pour l'application. Par exemple :
    ```sql
    CREATE DATABASE consommation_eau_db OWNER votre_utilisateur_pg;
    ```
    (Remplacez `votre_utilisateur_pg` par le nom d'utilisateur PostgreSQL qui sera utilisé par WildFly).
3.  Connectez-vous à la base de données nouvellement créée :
    ```sql
    \c consommation_eau_db
    ```
4.  Exécutez le script SQL fourni `schema.sql` pour créer les tables nécessaires. Ce script se trouve à la racine du projet décompressé.
    ```sql
    \i /chemin/vers/votre/projet/schema.sql
    ```
    (Adaptez le chemin vers le fichier `schema.sql`).

### 2. Configuration de la DataSource dans WildFly

1.  **Ajouter le Driver PostgreSQL à WildFly :**
    *   Téléchargez le driver JDBC PostgreSQL (fichier `.jar`, par exemple `postgresql-42.7.3.jar`) depuis le site officiel de PostgreSQL ou Maven Central.
    *   Copiez ce fichier `.jar` dans le répertoire `modules` de WildFly. Une structure typique serait :
        `WILDFLY_HOME/modules/system/layers/base/org/postgresql/main/`
    *   Créez un fichier `module.xml` dans ce répertoire (`WILDFLY_HOME/modules/system/layers/base/org/postgresql/main/module.xml`) avec le contenu suivant (adaptez le nom du jar si nécessaire) :
        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <module xmlns="urn:jboss:module:1.9" name="org.postgresql">
            <resources>
                <resource-root path="postgresql-42.7.3.jar"/>
            </resources>
            <dependencies>
                <module name="jakarta.jdbc"/>
                <module name="jakarta.transaction.api"/>
            </dependencies>
        </module>
        ```
    *   **Alternative (plus simple) :** Utilisez la commande `module add` de `jboss-cli` (voir documentation WildFly).

2.  **Enregistrer le Driver et Créer la DataSource via `jboss-cli` :**
    *   Démarrez le serveur WildFly.
    *   Lancez l'outil `jboss-cli` :
        *   Linux/macOS : `WILDFLY_HOME/bin/jboss-cli.sh --connect`
        *   Windows : `WILDFLY_HOME\bin\jboss-cli.bat --connect`
    *   Exécutez les commandes suivantes dans `jboss-cli` (adaptez les valeurs si nécessaire : nom de la base, utilisateur, mot de passe) :
        ```cli
        # Enregistrer le driver JDBC
        /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql, driver-module-name=org.postgresql, driver-class-name=org.postgresql.Driver)

        # Créer la DataSource JTA
        data-source add --name=ConsommationDeauDS --jndi-name=java:jboss/datasources/ConsommationDeauDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/consommation_eau_db --user-name=votre_utilisateur_pg --password=votre_mot_de_passe_pg --validate-on-match=true --background-validation=false --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter --jta=true --use-ccm=true --enabled=true

        # Recharger la configuration (ou redémarrer WildFly)
        :reload
        ```
    *   **Important :** Remplacez `localhost:5432`, `consommation_eau_db`, `votre_utilisateur_pg`, et `votre_mot_de_passe_pg` par les valeurs correspondant à votre configuration PostgreSQL.

### 3. Compilation et Packaging de l'Application (.war)

1.  Ouvrez un terminal ou une invite de commande.
2.  Naviguez jusqu'au répertoire racine du projet `consommationDeau` (où se trouve le fichier `pom.xml`).
3.  Exécutez la commande Maven pour compiler et packager l'application :
    ```bash
    mvn clean package
    ```
4.  Cette commande va générer le fichier `consommationDeau.war` dans le sous-répertoire `target/`.

### 4. Déploiement de l'Application sur WildFly

1.  Assurez-vous que le serveur WildFly est démarré.
2.  Copiez le fichier `consommationDeau.war` (généré à l'étape précédente) dans le répertoire `deployments` de WildFly :
    `WILDFLY_HOME/standalone/deployments/`
3.  WildFly détectera automatiquement le fichier `.war` et déploiera l'application. Surveillez la console ou le fichier `server.log` de WildFly pour vérifier que le déploiement s'est bien déroulé (recherchez des messages comme `Deployed "consommationDeau.war"`).

### 5. Accès à l'Application

Une fois l'application déployée avec succès, vous pouvez y accéder via votre navigateur web à l'adresse suivante (par défaut) :

[http://localhost:8080/consommationDeau/](http://localhost:8080/consommationDeau/)

Vous devriez voir la page de connexion.

## Structure du Projet

Le projet suit une structure Maven standard pour les applications web :

*   `src/main/java`: Code source Java (Modèles, DAO, Services, Contrôleurs, Filtres).
*   `src/main/resources`: Fichiers de ressources (ex: `META-INF/persistence.xml`).
*   `src/main/webapp`: Fichiers web (JSP, CSS, JS, images).
    *   `WEB-INF/views`: Contient les fichiers JSP.
    *   `WEB-INF/web.xml`: Descripteur de déploiement web (optionnel avec les annotations).
*   `pom.xml`: Fichier de configuration Maven (dépendances, build).
*   `schema.sql`: Script SQL pour la création de la base de données.
*   `README.md`: Ce fichier.

## Auteurs

*   Manus (AI Agent)


