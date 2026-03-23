# Documentation Technique - HSP-JAVA

## Plateforme de Gestion de Service d'Urgence Hospitalier

---

## 1. Prerequis et environnement

### 1.1 Technologies utilisees

| Composant        | Technologie       | Version   |
|------------------|-------------------|-----------|
| Langage          | Java              | 17        |
| Interface        | JavaFX (FXML)     | 17.0.6    |
| Base de donnees  | MySQL             | 8.0+      |
| Build            | Maven             | 3.8+      |
| Hachage MDP      | jBCrypt           | 0.4       |
| Export PDF        | Apache PDFBox     | 3.0.4     |
| QR Code (2FA)    | Google ZXing      | 3.5.3     |
| Tests unitaires  | JUnit 5           | 5.10.2    |
| Mocks            | Mockito           | 5.11.0    |

### 1.2 Prerequis d'installation

- **JDK 17** ou superieur
- **MySQL 8.0+** ou **MariaDB 10.5+**
- **Maven 3.8+**
- Un IDE Java (IntelliJ IDEA, Eclipse ou NetBeans)

### 1.3 Installation et lancement

#### Etape 1 : Cloner le depot

```bash
git clone <url-du-depot>
cd HSP-JAVA
```

#### Etape 2 : Creer la base de donnees

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p hsp_java < src/main/resources/sql/seed.sql
```

Le script `schema.sql` cree la base `hsp_java` avec toutes les tables, vues et index.
Le script `seed.sql` insere des donnees de demonstration.

Pour activer la 2FA :
```bash
mysql -u root -p hsp_java < src/main/resources/sql/migration_2fa.sql
```

#### Etape 3 : Configurer la connexion BDD

Modifier le fichier `src/main/java/appli/util/DBConnection.java` avec vos parametres :

```java
private static final String URL = "jdbc:mysql://localhost:3306/hsp_java";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

#### Etape 4 : Compiler et lancer

```bash
mvn clean javafx:run
```

#### Comptes de demonstration (seed.sql)

| Email                    | Mot de passe | Role         |
|--------------------------|--------------|--------------|
| admin@hsp.fr             | Admin@123    | ADMIN        |
| secretaire@hsp.fr        | Secret@123   | SECRETAIRE   |
| medecin@hsp.fr           | Medecin@123  | MEDECIN      |
| gestionnaire@hsp.fr      | Gestio@123   | GESTIONNAIRE |

---

## 2. Architecture du projet

### 2.1 Pattern architectural : MVC + Service + Repository

L'application suit une architecture en couches inspiree du MVC :

```
+-------------------------------------------------------+
|                    COUCHE PRESENTATION                  |
|  (JavaFX Controllers + FXML Views + CSS)               |
+-------------------------------------------------------+
                          |
                          v
+-------------------------------------------------------+
|                    COUCHE SERVICE                       |
|  (Logique metier, transactions, securite)              |
+-------------------------------------------------------+
                          |
                          v
+-------------------------------------------------------+
|                    COUCHE ACCES DONNEES                 |
|  (DAO + Repository JDBC)                               |
+-------------------------------------------------------+
                          |
                          v
+-------------------------------------------------------+
|                    BASE DE DONNEES                      |
|  (MySQL / InnoDB)                                      |
+-------------------------------------------------------+
```

### 2.2 Organisation des packages

```
src/main/java/appli/
|
+-- StartApplication.java          # Point d'entree de l'application
|
+-- model/                         # 17 entites metier (POJO)
|   +-- User.java                  # Utilisateur (4 roles)
|   +-- Patient.java               # Fiche patient
|   +-- DossierPriseEnCharge.java  # Dossier de triage
|   +-- Chambre.java               # Chambre d'hospitalisation
|   +-- Hospitalisation.java       # Sejour hospitalier
|   +-- Ordonnance.java            # Prescription medicale
|   +-- LigneOrdonnance.java       # Ligne de prescription
|   +-- Produit.java               # Produit medical
|   +-- Stock.java                 # Stock par lot/emplacement
|   +-- EmplacementStock.java      # Emplacement de stockage
|   +-- MouvementStock.java        # Mouvement d'entree/sortie
|   +-- DemandeProduit.java        # Demande medecin -> gestionnaire
|   +-- Fournisseur.java           # Fournisseur
|   +-- ProduitFournisseur.java    # Association produit-fournisseur (prix)
|   +-- CommandeFournisseur.java   # Bon de commande
|   +-- LigneCommande.java         # Ligne de commande
|   +-- JournalAction.java         # Entree du journal d'audit
|   +-- Alerte.java                # Alerte systeme
|   +-- Rendezvous.java            # Rendez-vous medical
|
+-- dao/                           # 20 Data Access Objects (JDBC pur)
|   +-- PatientDAO.java
|   +-- DossierPriseEnChargeDAO.java
|   +-- ChambreDAO.java
|   +-- HospitalisationDAO.java
|   +-- OrdonnanceDAO.java
|   +-- LigneOrdonnanceDAO.java
|   +-- ProduitDAO.java
|   +-- StockDAO.java
|   +-- MouvementStockDAO.java
|   +-- DemandeProduitDAO.java
|   +-- FournisseurDAO.java
|   +-- ProduitFournisseurDAO.java
|   +-- CommandeFournisseurDAO.java
|   +-- LigneCommandeDAO.java
|   +-- AlerteDAO.java
|   +-- RendezvousDAO.java
|   +-- ...
|
+-- repository/                    # Interfaces d'abstraction
|   +-- UserRepository.java
|   +-- PatientRepository.java
|   +-- JournalActionRepository.java
|   +-- StockRepository.java
|   +-- ProduitRepository.java
|   +-- FournisseurRepository.java
|   +-- jdbc/                      # Implementations JDBC
|       +-- PatientRepositoryJdbc.java
|       +-- StockRepositoryJdbc.java
|       +-- ProduitRepositoryJdbc.java
|       +-- FournisseurRepositoryJdbc.java
|
+-- service/                       # 9 services metier
|   +-- AuthService.java           # Authentification, 2FA, verrouillage
|   +-- MedicalService.java        # Ordonnances, hospitalisations, sorties
|   +-- StockService.java          # Produits, demandes, commandes, reapprovisionnement
|   +-- PatientService.java        # CRUD patients
|   +-- TriageService.java         # Creation dossiers de prise en charge
|   +-- JournalService.java        # Journalisation RGPD
|   +-- StatistiquesService.java   # Requetes analytiques
|   +-- PDFExportService.java      # Generation de PDF
|   +-- AlerteService.java         # Gestion des alertes
|
+-- security/                      # Securite
|   +-- SessionManager.java        # Singleton : session utilisateur
|   +-- RoleGuard.java             # RBAC : 18 permissions par role
|   +-- PasswordHasher.java        # Hachage BCrypt (cost 12)
|   +-- TotpService.java           # 2FA TOTP (RFC 6238)
|
+-- ui/controller/                 # 19 controleurs JavaFX
|   +-- LoginController.java
|   +-- DashboardController.java
|   +-- PatientsController.java
|   +-- TriageController.java
|   +-- DossierController.java
|   +-- HospitalisationsController.java
|   +-- ChambresController.java
|   +-- OrdonnancesController.java
|   +-- DemandesController.java
|   +-- StockController.java
|   +-- CommandesController.java
|   +-- FournisseursController.java
|   +-- RendezvousController.java
|   +-- UtilisateursController.java
|   +-- JournalController.java
|   +-- LoginLogController.java
|   +-- StatistiquesController.java
|   +-- TotpSetupController.java
|   +-- TotpVerifyController.java
|
+-- util/                          # Utilitaires
    +-- DBConnection.java          # Singleton : connexion MySQL
    +-- Router.java                # Navigation entre vues + controle d'acces
    +-- Route.java                 # Enum des 19 routes
    +-- PasswordValidator.java     # Validation force mot de passe

src/main/resources/
+-- appli/view/                    # 19 fichiers FXML + CSS
|   +-- login.fxml
|   +-- dashboard.fxml
|   +-- patients.fxml
|   +-- triage.fxml
|   +-- dossier.fxml
|   +-- hospitalisations.fxml
|   +-- chambres.fxml
|   +-- ordonnances.fxml
|   +-- demandes.fxml
|   +-- stock.fxml
|   +-- commandes.fxml
|   +-- fournisseurs.fxml
|   +-- rendezvous.fxml
|   +-- utilisateurs.fxml
|   +-- journal.fxml
|   +-- loginlog.fxml
|   +-- statistiques.fxml
|   +-- totp_setup.fxml
|   +-- totp_verify.fxml
|   +-- style.css
|
+-- sql/
    +-- schema.sql                 # Schema complet de la BDD
    +-- seed.sql                   # Donnees de demonstration
    +-- migration_2fa.sql          # Migration pour la 2FA
```

---

## 3. Modele de donnees

### 3.1 Schema de la base de donnees

La base contient **21 tables**, **4 vues** et utilise le moteur **InnoDB** avec le charset **utf8mb4**.

### 3.2 Diagramme des tables et relations

```
+----------------+       +---------------------------+       +--------------------+
|    users       |       | dossiers_prise_en_charge  |       |    patients        |
+----------------+       +---------------------------+       +--------------------+
| id (PK)        |<--+   | id (PK)                   |------>| id (PK)            |
| email (UNIQUE) |   |   | numero_dossier (UNIQUE)   |       | numero_secu (UNI)  |
| password_hash  |   |   | patient_id (FK)           |       | nom, prenom        |
| nom, prenom    |   |   | date_creation             |       | date_naissance     |
| role (ENUM)    |   +---| medecin_responsable_id(FK)|       | sexe, groupe_sang  |
| totp_secret    |   |   | cree_par (FK)             |       | adresse, email     |
| totp_enabled   |   |   | niveau_gravite (1-5)      |       | telephone          |
| actif           |   |   | symptomes                 |       | allergies          |
| verrouille      |   |   | statut (7 etats)          |       | antecedents        |
| tentatives     |   |   | destination_sortie        |       | cree_par (FK)      |
+----------------+   |   +---------------------------+       +--------------------+
       |              |              |
       |              |              | 1:N
       |              |              v
       |              |   +--------------------+       +------------------+
       |              |   | hospitalisations   |------>| chambres         |
       |              |   +--------------------+       +------------------+
       |              +---| medecin_id (FK)    |       | id (PK)          |
       |                  | dossier_id (FK)    |       | numero (UNIQUE)  |
       |                  | chambre_id (FK)    |       | type_chambre     |
       |                  | numero_sejour(UNI) |       | capacite         |
       |                  | date_entree        |       | nb_lits_occupes  |
       |                  | statut             |       | etage, batiment  |
       |                  | type_sortie        |       | en_maintenance   |
       |                  +--------------------+       +------------------+
       |
       |              +--------------------+
       |              | ordonnances        |
       |              +--------------------+
       +--------------| medecin_id (FK)    |
       |              | dossier_id (FK)    |
       |              | numero_ordo (UNI)  |
       |              | statut             |
       |              | date_debut/fin     |
       |              +--------------------+
       |                       | 1:N
       |                       v
       |              +---------------------+       +-----------------+
       |              | lignes_ordonnance   |------>| produits        |
       |              +---------------------+       +-----------------+
       |              | ordonnance_id (FK)  |       | id (PK)         |
       |              | produit_id (FK)     |       | code (UNIQUE)   |
       |              | posologie           |       | nom             |
       |              | quantite            |       | dangerosite     |
       |              | voie_administration |       | forme           |
       |              +---------------------+       | seuil_alerte    |
       |                                            | categorie_id(FK)|
       |                                            +-----------------+
       |                                                   |
       |              +--------------------+               | N:N
       +--------------| demandes_produits  |               v
       |              +--------------------+    +------------------------+
       |              | medecin_id (FK)    |    | produits_fournisseurs  |
       |              | gestionnaire_id(FK)|    +------------------------+
       |              | produit_id (FK)    |    | produit_id (FK)        |
       |              | quantite_demandee  |    | fournisseur_id (FK)    |
       |              | statut (7 etats)   |    | prix_achat             |
       |              | urgence            |    | delai_livraison        |
       |              +--------------------+    +------------------------+
       |                                                   |
       |                                                   v
       |              +--------------------+    +--------------------+
       |              | stocks             |    | fournisseurs       |
       |              +--------------------+    +--------------------+
       |              | produit_id (FK)    |    | id (PK)            |
       |              | emplacement_id(FK) |    | code (UNIQUE)      |
       |              | lot                |    | nom, siret         |
       |              | quantite           |    | adresse, email     |
       |              | quantite_reservee  |    | note_evaluation    |
       |              | date_peremption    |    +--------------------+
       |              | fournisseur_id(FK) |           |
       |              +--------------------+           v
       |                       |            +---------------------------+
       |                       v            | commandes_fournisseurs    |
       |              +--------------------++---------------------------+
       |              | mouvements_stock   || fournisseur_id (FK)       |
       |              +--------------------+| numero_commande (UNI)     |
       +--------------| user_id (FK)       || statut (7 etats)          |
       |              | stock_id (FK)      || montant_ht/tva/ttc        |
       |              | type_mouvement     || createur_id (FK)          |
       |              | quantite_avant     |+---------------------------+
       |              | quantite_apres     |           | 1:N
       |              +--------------------+           v
       |                                    +--------------------+
       |                                    | lignes_commande    |
       |                                    +--------------------+
       |                                    | commande_id (FK)   |
       |                                    | produit_id (FK)    |
       |                                    | quantite_commandee |
       |                                    | prix_unitaire      |
       |                                    +--------------------+
       |
       |              +--------------------+
       +--------------| journal_actions    |
       |              +--------------------+
       |              | user_id (FK)       |
       |              | type_action        |
       |              | description        |
       |              | date_action        |
       |              | adresse_ip         |
       |              | entite, entite_id  |
       |              | donnees_avant(JSON)|
       |              | donnees_apres(JSON)|
       |              +--------------------+
       |
       |              +--------------------+       +---------------------+
       +--------------| rendezvous         |       | emplacements_stock  |
       |              +--------------------+       +---------------------+
       |              | patient_id (FK)    |       | id (PK)             |
       |              | medecin_id (FK)    |       | code (UNIQUE)       |
       |              | date_heure         |       | type_emplacement    |
       |              | type_rdv           |       | temperature_ctrl    |
       |              | statut             |       +---------------------+
       |              +--------------------+
       |
       |              +--------------------+
       +--------------| alertes            |
                      +--------------------+
                      | type_alerte        |
                      | niveau             |
                      | titre, message     |
                      | entite, entite_id  |
                      | lu_par (FK)        |
                      | resolu_par (FK)    |
                      +--------------------+
```

### 3.3 Vues SQL

| Vue                     | Description                                          |
|-------------------------|------------------------------------------------------|
| `v_stocks_details`      | Stock disponible, jours avant peremption, statut     |
| `v_dossiers_triage`     | File d'attente triee par gravite et priorite         |
| `v_occupation_chambres` | Taux d'occupation par chambre                        |
| `v_rendezvous_a_venir`  | Rendez-vous futurs non annules                       |

---

## 4. Couche securite

### 4.1 SessionManager (Singleton)

```
appli/security/SessionManager.java
```

Gere la session de l'utilisateur connecte en memoire.

- `login(User)` : stocke l'utilisateur courant
- `logout()` : efface la session
- `isLoggedIn()` : verifie si une session est active
- `hasRole(Role)`, `isAdmin()`, `isMedecin()`, `isSecretaire()`, `isGestionnaire()` : helpers de role

### 4.2 RoleGuard (RBAC)

```
appli/security/RoleGuard.java
```

Matrice de permissions statique definissant 18 fonctionnalites par role :

| Fonctionnalite            | ADMIN | SECRETAIRE | MEDECIN | GESTIONNAIRE |
|---------------------------|-------|------------|---------|--------------|
| GESTION_UTILISATEURS      | X     |            |         |              |
| GESTION_PATIENTS          | X     | X          |         |              |
| CONSULTATION_PATIENTS     | X     | X          | X       |              |
| GESTION_DOSSIERS          | X     | X          | X       |              |
| TRIAGE                    | X     | X          |         |              |
| GESTION_HOSPITALISATIONS  | X     |            | X       |              |
| GESTION_ORDONNANCES       | X     |            | X       |              |
| DEMANDE_PRODUITS          | X     |            | X       |              |
| GESTION_STOCK             | X     |            |         | X            |
| VALIDATION_DEMANDES       | X     |            |         | X            |
| GESTION_COMMANDES         | X     |            |         | X            |
| GESTION_FOURNISSEURS      | X     |            |         | X            |
| GESTION_CHAMBRES          | X     |            |         |              |
| GESTION_RENDEZ_VOUS       | X     | X          | X       |              |
| CONSULTATION_JOURNAL      | X     |            |         |              |
| CONSULTATION_LOGIN_LOG    | X     |            |         |              |
| CONSULTATION_STATISTIQUES | X     | X          | X       | X            |
| EXPORT_DONNEES            | X     |            | X       | X            |

La methode `canAccessView(String viewName)` est appelee par le Router avant chaque navigation pour verifier les droits.

### 4.3 PasswordHasher (BCrypt)

```
appli/security/PasswordHasher.java
```

- `hash(String password)` : genere un hash BCrypt avec salt integre, facteur de cout 12
- `verify(String password, String storedHash)` : verifie un mot de passe contre le hash stocke

### 4.4 TotpService (2FA)

```
appli/security/TotpService.java
```

Implementation TOTP conforme RFC 6238 :
- Cles secretes encodees en Base32
- Codes a 6 chiffres, pas de temps de 30 secondes
- Tolerance de +/- 1 fenetre temporelle
- Generation d'URI OTP Auth pour QR codes (compatible Google Authenticator)

### 4.5 PasswordValidator

```
appli/util/PasswordValidator.java
```

Politique de mots de passe forts :
- Minimum 8 caracteres
- Au moins 1 majuscule (`[A-Z]`)
- Au moins 1 minuscule (`[a-z]`)
- Au moins 1 chiffre (`\d`)
- Au moins 1 caractere special (`@#$%^&+=!`)

---

## 5. Couche service (logique metier)

### 5.1 AuthService

**Responsabilites** : authentification, gestion des comptes, 2FA

Flux d'authentification :
```
login(email, password)
  |
  +-> Email inconnu ? -> IDENTIFIANTS_INVALIDES
  +-> Compte desactive ? -> COMPTE_DESACTIVE
  +-> Compte verrouille ?
  |     +-> Delai ecoule ? -> Deverrouiller et continuer
  |     +-> Delai non ecoule ? -> COMPTE_VERROUILLE
  +-> Mot de passe incorrect ?
  |     +-> Incrementer tentatives
  |     +-> >= 5 tentatives ? -> Verrouiller compte
  |     +-> IDENTIFIANTS_INVALIDES
  +-> 2FA activee ? -> TOTP_REQUIRED (demander le code)
  +-> SUCCESS (session ouverte)
```

### 5.2 MedicalService

**Responsabilites** : ordonnances, hospitalisations, sorties

Operations transactionnelles (avec `SELECT FOR UPDATE`) :
- `hospitaliser()` : verrouille la chambre, verifie la disponibilite, cree le sejour, incremente les lits occupes
- `sortiePatient()` : termine l'hospitalisation, libere le lit, cloture le dossier

### 5.3 StockService

**Responsabilites** : produits, fournisseurs, demandes, commandes, reapprovisionnement

Operations transactionnelles :
- `validerDemande()` : verrouille les stocks (`SELECT FOR UPDATE`), decremente par lot (FEFO - First Expired First Out), cree les mouvements de sortie
- `reapprovisionner()` : verifie si le lot existe, met a jour ou cree le stock, enregistre le mouvement d'entree
- `recevoirLivraison()` : traite chaque ligne de commande, reapprovisionne le stock, met a jour le statut de la commande

### 5.4 JournalService

**Responsabilites** : journalisation conforme RGPD

- `logConnexionReussie()` / `logConnexionEchec()` : trace les tentatives de connexion
- `logAction()` : trace les operations CRUD avec snapshots avant/apres
- `purgeOldLogs(jours)` : politique de retention des donnees

### 5.5 StatistiquesService

**Responsabilites** : requetes analytiques pour les tableaux de bord

- `getIndicateurs()` : patients hospitalises, dossiers en attente, stocks critiques, taux d'occupation
- `getHospitalisationsParSemaine()` : tendance sur 8 semaines
- `getTauxOccupationParType()` : par type de chambre
- `getRepartitionGravite()` : distribution des niveaux 1 a 5
- `getProduitsLesPlusDemandes(limit)` : classement des produits

### 5.6 PDFExportService

**Responsabilites** : generation de documents PDF avec Apache PDFBox

- `exportFichePatient()` : fiche patient complete en A4
- `exportDossierPriseEnCharge()` : dossier multi-pages avec ordonnances et hospitalisation
- `exportRapportStatistiques()` : rapport avec indicateurs et graphiques

---

## 6. Navigation et routage

### 6.1 Router

```
appli/util/Router.java
```

Gestionnaire central de navigation :
- `goTo(Route route)` : navigue vers une vue apres verification des permissions
- `goTo(Route route, Object data)` : navigation avec donnees transitoires
- `logout()` : deconnexion et retour au login
- Integre le `RoleGuard` : verifie `canAccessView()` avant chaque chargement de vue

### 6.2 Route (Enum)

```
appli/util/Route.java
```

19 routes definies, chacune associee a un fichier FXML et un titre de fenetre :

| Route           | Vue FXML              | Titre                         |
|-----------------|-----------------------|-------------------------------|
| LOGIN           | login.fxml            | HSP - Connexion               |
| DASHBOARD       | dashboard.fxml        | HSP - Tableau de bord         |
| PATIENTS        | patients.fxml         | HSP - Gestion des Patients    |
| TRIAGE          | triage.fxml           | HSP - Nouveau Cas de Triage   |
| DOSSIER         | dossier.fxml          | HSP - Detail du Dossier       |
| HOSPITALISATIONS| hospitalisations.fxml | HSP - Hospitalisations        |
| CHAMBRES        | chambres.fxml         | HSP - Gestion des Chambres    |
| ORDONNANCES     | ordonnances.fxml      | HSP - Ordonnances             |
| DEMANDES        | demandes.fxml         | HSP - Demandes de Produits    |
| STOCK           | stock.fxml            | HSP - Gestion du Stock        |
| COMMANDES       | commandes.fxml        | HSP - Commandes Fournisseurs  |
| FOURNISSEURS    | fournisseurs.fxml     | HSP - Gestion des Fournisseurs|
| RENDEZ_VOUS     | rendezvous.fxml       | HSP - Rendez-vous             |
| UTILISATEURS    | utilisateurs.fxml     | HSP - Gestion des Utilisateurs|
| JOURNAL         | journal.fxml          | HSP - Journal des Actions     |
| LOGIN_LOG       | loginlog.fxml         | HSP - Journal des Connexions  |
| STATISTIQUES    | statistiques.fxml     | HSP - Statistiques            |
| TOTP_SETUP      | totp_setup.fxml       | HSP - Configuration 2FA       |
| TOTP_VERIFY     | totp_verify.fxml      | HSP - Verification 2FA        |

---

## 7. Gestion des transactions

Les operations critiques utilisent des transactions JDBC avec verrouillage pessimiste :

### Pattern utilise

```java
Connection conn = DBConnection.getInstance().getConnection();
conn.setAutoCommit(false);
try {
    // 1. SELECT ... FOR UPDATE (verrouillage de la ligne)
    // 2. Verifications metier
    // 3. Operations d'ecriture
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
    throw new RuntimeException(e);
} finally {
    conn.close();
}
```

### Operations transactionnelles

| Operation              | Service          | Verrouillage         |
|------------------------|------------------|----------------------|
| Hospitalisation        | MedicalService   | Chambre (FOR UPDATE) |
| Sortie patient         | MedicalService   | Chambre (FOR UPDATE) |
| Validation demande     | StockService     | Stocks (FOR UPDATE)  |
| Reapprovisionnement    | StockService     | Stock (FOR UPDATE)   |

---

## 8. Base de donnees

### 8.1 Configuration

- **Moteur** : InnoDB (transactions ACID, cles etrangeres)
- **Charset** : utf8mb4 (support complet Unicode)
- **Collation** : utf8mb4_unicode_ci

### 8.2 Contraintes d'integrite

- **Cles etrangeres** avec `ON DELETE RESTRICT` pour les relations critiques (ex: patient -> dossier)
- **Contraintes CHECK** : `nb_lits_occupes <= capacite`, `quantite >= 0`, `quantite_reservee <= quantite`
- **Index** sur les colonnes frequemment recherchees/filtrees (nom, statut, date, role, etc.)
- **Contraintes UNIQUE** sur les identifiants metier (email, n° secu, n° dossier, n° commande, etc.)

### 8.3 Vues SQL

Les 4 vues pre-calculent des donnees frequemment utilisees :

- `v_stocks_details` : quantite disponible, jours avant peremption, statut stock (RUPTURE/ALERTE/PEREMPTION/OK)
- `v_dossiers_triage` : file d'attente triee par gravite descendante puis anciennete
- `v_occupation_chambres` : lits disponibles et taux d'occupation par chambre
- `v_rendezvous_a_venir` : rendez-vous futurs non annules, tries par date

---

## 9. Tests

### 9.1 Framework

- **JUnit 5** pour les assertions et le cycle de vie des tests
- **Mockito 5** pour les mocks des couches DAO/Repository

### 9.2 Classes de tests

| Classe               | Package         | Couverture                              |
|----------------------|-----------------|-----------------------------------------|
| MedicalServiceTest   | appli.service   | Ordonnances, hospitalisations, sorties  |
| StockServiceTest     | appli.service   | Validation/refus demandes, livraisons   |

### 9.3 Lancer les tests

```bash
mvn test
```

Configuration JVM requise pour les tests (configuree dans pom.xml) :
```
--add-opens appli.hsp/appli.service=ALL-UNNAMED
--add-opens appli.hsp/appli.dao=ALL-UNNAMED
--add-opens appli.hsp/appli.model=ALL-UNNAMED
--add-opens appli.hsp/appli.security=ALL-UNNAMED
```

---

## 10. Choix techniques et justifications

| Choix                        | Justification                                                |
|------------------------------|--------------------------------------------------------------|
| **Java 17**                  | LTS, records, text blocks, switch expressions, pattern matching |
| **JavaFX 17 + FXML**        | Separation claire vue/controleur, scene builder compatible    |
| **MySQL / InnoDB**           | Transactions ACID, cles etrangeres, fiable et repandu        |
| **BCrypt (cost 12)**         | Standard industriel pour le hachage de mots de passe, resistant aux attaques GPU |
| **TOTP (RFC 6238)**          | Standard ouvert pour la 2FA, compatible avec toutes les apps d'authentification |
| **Apache PDFBox**            | Bibliotheque Java native pour la generation de PDF, sans dependance externe |
| **ZXing**                    | Reference pour la generation de QR codes en Java             |
| **JDBC pur (pas d'ORM)**     | Controle fin des requetes SQL, optimisation des transactions  |
| **Pattern Repository**       | Abstraction de l'acces donnees, facilite les tests avec mocks |
| **Singleton (DBConnection)** | Reutilisation de la connexion, evite les fuites de ressources |
| **Maven**                    | Build standardise, gestion automatique des dependances        |

---

## 11. Securite - Prevention des vulnerabilites

| Vulnerabilite        | Protection mise en place                                     |
|----------------------|--------------------------------------------------------------|
| **Injection SQL**    | Requetes preparees (PreparedStatement) dans tous les DAO     |
| **Brute force**      | Verrouillage apres 5 tentatives, delai de 30 minutes        |
| **Mots de passe faibles** | Validation : 8 car. min, majuscule, minuscule, chiffre, special |
| **Stockage MDP**     | BCrypt avec salt integre (jamais en clair)                   |
| **Acces non autorise**| RBAC avec 18 permissions, verification a chaque navigation   |
| **Tracabilite**      | Journal complet avec IP, user agent, snapshots avant/apres   |
| **Concurrence**      | Transactions + SELECT FOR UPDATE pour les operations critiques |

---

## 12. Deploiement

### Build de production

```bash
mvn clean package
```

### Distribution native (JLink)

Le plugin `javafx-maven-plugin` est configure pour generer une image JLink :
```bash
mvn javafx:jlink
```

Cela produit une image autonome dans `target/app/` qui inclut le JRE minimal necessaire.
