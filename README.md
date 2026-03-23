# HSP-JAVA - Plateforme de Gestion de Service d'Urgence Hospitalier

Application de bureau en Java/JavaFX permettant de gerer un service d'urgence hospitalier : prise en charge des patients, gestion des stocks de produits medicaux, et tracabilite des actions conformement au RGPD.

---

## Fonctionnalites principales

### Gestion des patients
- Creation et modification de fiches patients (nom, prenom, n° securite sociale, email, telephone, adresse)
- Enregistrement de dossiers de prise en charge avec niveau de gravite (1 a 5), symptomes, constantes vitales
- File de triage triee par gravite et priorite

### Gestion medicale
- Consultation et prise en charge des dossiers en salle d'attente
- Hospitalisation des patients avec controle de disponibilite des chambres (transaction securisee)
- Prescription d'ordonnances avec lignes de medicaments (posologie, voie d'administration, duree)
- Liberation des chambres a la sortie des patients
- Gestion des rendez-vous medicaux

### Gestion des stocks
- Fiches produits avec niveau de dangerosite (1 a 5) et seuils d'alerte
- Association produit-fournisseur avec prix d'achat
- Demandes de produits par les medecins avec workflow de validation/refus
- Reapprovisionnement via commandes fournisseurs et reception de livraisons
- Alertes automatiques (stock bas, rupture, peremption)

### Securite et RGPD
- 4 roles : Administrateur, Secretaire, Medecin, Gestionnaire de stock
- Controle d'acces RBAC avec 18 permissions granulaires
- Mots de passe securises (BCrypt, cost 12) avec politique de complexite
- Double authentification TOTP (compatible Google Authenticator, Authy)
- Verrouillage de compte apres 5 tentatives echouees
- Journal complet des actions avec snapshots avant/apres
- Historique des connexions avec adresse IP
- Creation de comptes reservee a l'administrateur

### Statistiques et exports
- Tableau de bord : patients hospitalises, dossiers en attente, stocks critiques, taux d'occupation
- Graphiques : hospitalisations par semaine, repartition par gravite, produits les plus demandes
- Export PDF : fiches patients, dossiers de prise en charge, rapports statistiques

---

## Technologies

| Composant       | Technologie          | Version |
|-----------------|----------------------|---------|
| Langage         | Java                 | 17      |
| Interface       | JavaFX (FXML)        | 17.0.6  |
| Base de donnees | MySQL                | 8.0+    |
| Build           | Maven                | 3.8+    |
| Hachage MDP     | jBCrypt              | 0.4     |
| Export PDF       | Apache PDFBox        | 3.0.4   |
| QR Code (2FA)   | Google ZXing         | 3.5.3   |
| Tests           | JUnit 5 + Mockito 5  |         |

---

## Prerequis

- **JDK 17** ou superieur
- **MySQL 8.0+** ou **MariaDB 10.5+**
- **Maven 3.8+**

---

## Installation

### 1. Cloner le depot

```bash
git clone <url-du-depot>
cd HSP-JAVA
```

### 2. Creer la base de donnees

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p hsp_java < src/main/resources/sql/seed.sql
```

### 3. Configurer la connexion

Modifier les parametres dans `src/main/java/appli/util/DBConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/hsp_java";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

### 4. Compiler et lancer

```bash
mvn clean javafx:run
```

### 5. Lancer les tests

```bash
mvn test
```

---

## Comptes de demonstration

| Email                 | Mot de passe | Role           |
|-----------------------|--------------|----------------|
| admin@hsp.fr          | Admin@123    | Administrateur |
| secretaire@hsp.fr     | Secret@123   | Secretaire     |
| medecin@hsp.fr        | Medecin@123  | Medecin        |
| gestionnaire@hsp.fr   | Gestio@123   | Gestionnaire   |

---

## Architecture

Le projet suit une architecture **MVC** avec couche Service et Repository :

```
src/main/java/appli/
├── model/          17 entites metier
├── dao/            20 Data Access Objects (JDBC)
├── repository/     Interfaces + implementations JDBC
├── service/        9 services metier (transactions, logique)
├── security/       Authentification, RBAC, BCrypt, TOTP
├── ui/controller/  19 controleurs JavaFX
└── util/           Connexion BDD, routage, validation

src/main/resources/
├── appli/view/     19 fichiers FXML + CSS
└── sql/            Schema, seed, migrations
```

---

## Documentation

La documentation complete est disponible dans le dossier `docs/` :

- **[Documentation fonctionnelle](docs/DOCUMENTATION_FONCTIONNELLE.md)** : guide utilisateur, profils et permissions, workflows metier, securite, RGPD
- **[Documentation technique](docs/DOCUMENTATION_TECHNIQUE.md)** : architecture, modele de donnees, couche securite, services, transactions, deploiement

---

## Structure de la base de donnees

- **21 tables** : users, patients, dossiers_prise_en_charge, chambres, hospitalisations, ordonnances, lignes_ordonnance, produits, stocks, mouvements_stock, demandes_produits, fournisseurs, produits_fournisseurs, commandes_fournisseurs, lignes_commande, emplacements_stock, categories_produits, alertes, journal_actions, rendezvous
- **4 vues** : v_stocks_details, v_dossiers_triage, v_occupation_chambres, v_rendezvous_a_venir
- **Moteur** : InnoDB (transactions ACID)
- **Charset** : utf8mb4
