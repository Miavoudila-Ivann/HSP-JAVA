# HSP-JAVA - Hospital System Protocol

> Systeme de gestion hospitaliere complet developpe en **Java 17** avec **JavaFX**.
> Gestion du triage, des hospitalisations, des ordonnances, des stocks et de la conformite RGPD.

---

## Table des matieres

- [Fonctionnalites](#fonctionnalites)
- [Stack technique](#stack-technique)
- [Prerequis](#prerequis)
- [Installation](#installation)
- [Build et lancement](#build-et-lancement)
- [Roles et permissions](#roles-et-permissions)
- [Comptes de test](#comptes-de-test)
- [Architecture](#architecture)
- [Tests](#tests)
- [Scenario demo](#scenario-demo)

---

## Fonctionnalites

### Triage et accueil

- Creation de dossiers de prise en charge avec **5 niveaux de gravite** (Mineur, Modere, Grave, Severe, Critique)
- Mode d'arrivee (ambulance, pompiers, personnel, transfert)
- Saisie des constantes vitales, allergies et antecedents
- Code couleur par gravite pour un reperage visuel rapide
- Suivi du statut : EN_ATTENTE, EN_COURS, HOSPITALISE, TERMINE, TRANSFERE, ANNULE

### Hospitalisations

- Attribution de chambre avec **verrouillage transactionnel** (`SELECT FOR UPDATE`) pour eviter les doubles affectations
- 7 types de chambre : simple, double, soins intensifs, reanimation, urgence, pediatrie, maternite
- Suivi du sejour : diagnostic d'entree, plan de traitement, observations
- Sortie patient avec motif : guerison, amelioration, transfert, contre avis medical, deces
- Liberation automatique du lit a la sortie

### Ordonnances

- Prescription medicamenteuse avec posologie, voie d'administration et duree
- Voies supportees : orale, IV, IM, sous-cutanee, transdermique, inhalation, rectale, ophtalmique
- Gestion des **stupefiants** (flag dedie)
- Statuts : ACTIVE, TERMINEE, ANNULEE, SUSPENDUE
- Liaison aux hospitalisations ou dossiers de prise en charge

### Gestion des stocks

- Catalogue produits avec categories (medicaments, consommables, dispositifs medicaux)
- Emplacements multiples avec zones a temperature controlee
- Suivi des **lots** avec dates de peremption
- Systeme d'alertes pour les produits proches de la peremption
- Calcul quantite disponible vs quantite reservee
- Tarification fournisseur avec marge

### Demandes de produits

- Circuit complet : medecin &rarr; gestionnaire avec validation transactionnelle
- Workflow multi-etapes : EN_ATTENTE &rarr; VALIDEE &rarr; EN_PREPARATION &rarr; PRETE &rarr; LIVREE
- Support des livraisons partielles
- Signalement de demandes urgentes avec niveaux de priorite

### Mouvements de stock

- 6 types de mouvement : entree, sortie, transfert, casse, retour, consommation
- Tracabilite complete avec horodatage
- Liaison aux demandes ou aux commandes fournisseurs

### Securite et authentification

- Hachage des mots de passe avec **BCrypt** (10 rounds de sel)
- **Verrouillage de compte** apres 5 tentatives echouees (30 minutes)
- Gestion de session avec controle d'acces par role (RBAC)
- Garde de route sur chaque vue selon le role connecte

### Audit et conformite RGPD

- Journalisation de toutes les actions : connexion, deconnexion, CRUD, exports, echecs
- Snapshots JSON des donnees avant/apres modification
- Suivi de l'adresse IP et du user agent
- Horodatage UTC
- Recherche par utilisateur, plage de dates, type d'entite

---

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Langage | Java 17 |
| Interface | JavaFX 17.0.6 (FXML) |
| Base de donnees | MySQL 8.0+ (InnoDB, utf8mb4) |
| Driver JDBC | MySQL Connector/J 9.5.0 |
| Securite | jBCrypt 0.4 |
| Build | Maven 3.8+ |
| Tests | JUnit 5.10.2, Mockito 5.11.0 |

---

## Prerequis

- **JDK** 17 ou superieur
- **Maven** 3.8 ou superieur
- **MySQL** 8.0 ou superieur

---

## Installation

### 1. Cloner le depot

```bash
git clone <url-du-repo>
cd HSP-JAVA
```

### 2. Creer la base de donnees

```sql
CREATE DATABASE hsp_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Executer le schema

```bash
mysql -u root -p hsp_java < src/main/resources/sql/schema.sql
```

### 4. Charger les donnees de test

```bash
mysql -u root -p hsp_java < src/main/resources/sql/seed.sql
```

### 5. Configurer la connexion

La configuration se trouve dans `src/main/java/appli/util/DBConnection.java` :

```java
SERVEUR       = "localhost"
NOM_BDD       = "hsp_java"
UTILISATEUR   = "root"
MOT_DE_PASSE  = ""
```

Modifier ces valeurs selon votre environnement.

---

## Build et lancement

```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn javafx:run

# Lancer les tests
mvn test

# Generer le package
mvn package
```

---

## Roles et permissions

| Role | Acces | Fonctionnalites principales |
|------|-------|-----------------------------|
| **ADMIN** | Complet | Gestion des utilisateurs, consultation de toutes les donnees, parametrage |
| **SECRETAIRE** | Accueil | Creation de fiches patient, ouverture de dossiers de triage |
| **MEDECIN** | Clinique | Prise en charge des dossiers, hospitalisations, ordonnances, demandes de produits, sortie patient |
| **GESTIONNAIRE** | Stock | Gestion des produits et fournisseurs, validation/refus des demandes, mouvements de stock, reapprovisionnement |

---

## Comptes de test

Mot de passe commun : **`password123`**

| Email | Nom | Role |
|-------|-----|------|
| `admin@hsp.fr` | Jean Dupont | Administrateur |
| `secretaire1@hsp.fr` | Marie Martin | Secretaire |
| `secretaire2@hsp.fr` | Sophie Bernard | Secretaire |
| `medecin1@hsp.fr` | Pierre Leroy | Medecin generaliste |
| `medecin2@hsp.fr` | Claire Moreau | Medecin urgences |
| `medecin3@hsp.fr` | Francois Petit | Medecin cardiologue |
| `medecin4@hsp.fr` | Isabelle Roux | Medecin pediatre |
| `gestionnaire1@hsp.fr` | Michel Fournier | Gestionnaire stock |
| `gestionnaire2@hsp.fr` | Anne Girard | Gestionnaire stock |

---

## Architecture

```
src/main/java/appli/
├── model/              17 entites (User, Patient, Hospitalisation, Stock, Ordonnance...)
├── service/            6 services metier (MedicalService, StockService, AuthService...)
├── repository/         Interfaces + implementations JDBC
├── dao/                Acces donnees direct (couche legacy)
├── security/           Authentification, sessions, controle d'acces (RBAC)
├── ui/
│   ├── controller/     10 controleurs JavaFX
│   └── util/           Helpers (AlertHelper)
└── util/               DBConnection, Router, Route, ValidationUtils

src/main/resources/
├── sql/                schema.sql + seed.sql
└── appli/view/         11 fichiers FXML + CSS

src/test/java/appli/
└── service/            Tests unitaires (MedicalServiceTest, StockServiceTest)
```

### Couches applicatives

```
┌─────────────────────────────────────────┐
│          Presentation (JavaFX/FXML)     │
│         Controllers + Views + CSS       │
├─────────────────────────────────────────┤
│           Securite (RBAC)               │
│   SessionManager, RoleGuard, BCrypt     │
├─────────────────────────────────────────┤
│          Services (Logique metier)      │
│  MedicalService, StockService, etc.     │
├─────────────────────────────────────────┤
│        Repository (Acces donnees)       │
│     Interfaces + Implementations JDBC   │
├─────────────────────────────────────────┤
│             MySQL (InnoDB)              │
│   21 tables, FK, index, contraintes    │
└─────────────────────────────────────────┘
```

### Patterns utilises

- **MVC** : Model-View-Controller via JavaFX
- **Repository** : Interfaces d'acces aux donnees avec implementations JDBC
- **Singleton** : DBConnection, SessionManager
- **RBAC** : Controle d'acces par role sur chaque route
- **Transactions** : Verrouillage pessimiste pour l'attribution de chambres

---

## Tests

Les tests unitaires couvrent les regles metier critiques :

- **MedicalServiceTest** : hospitalisation (controles de securite, verification des etats, capacite des chambres), sortie patient
- **StockServiceTest** : validation de demandes (securite, etats, livraison partielle), refus de demandes

```bash
mvn test
```

---

## Scenario demo

Le fichier `seed.sql` injecte un scenario complet sur plusieurs jours.

### Jour 1 — 15 janvier

1. **Secretaire** Marie Martin accueille 4 patients avec des gravites differentes
2. **Dr Leroy** prend en charge Dubois (douleur thoracique, gravite 4) :
   - Hospitalisation en soins intensifs — chambre 301
   - Ordonnance : aspirine + adrenaline si besoin
   - Demande de seringues &rarr; **validee** par le gestionnaire
3. Demande de gants latex &rarr; **refusee** (rupture de stock)

### Jour 2 — 16 janvier

4. **Dr Petit** (cardiologue) prend en charge Moreau en urgence critique (IDM) :
   - Hospitalisation en reanimation — chambre 303
   - Ordonnance avec **morphine** (stupefiant) + aspirine + adrenaline
   - Demande urgente de morphine &rarr; **validee** depuis le coffre
5. **Dr Moreau** hospitalise Durand (fracture ouverte) — chambre 104
6. **Dr Roux** hospitalise Faure (menace accouchement premature) en maternite — chambre 403
7. **Dr Leroy** hospitalise Bernard (BPCO decompensee) — chambre 201
8. Reapprovisionnement compresses + transfert adrenaline frigo &rarr; urgences

### Jour 5 — 21 janvier

9. **Sortie de Bernard** avec amelioration : ordonnance de relais PO, cloture du dossier

### Etat final du scenario

| Donnee | Valeur |
|--------|--------|
| Patients hospitalises | 4 (Dubois en SI, Moreau en Rea, Durand en Chir, Faure en Mat) |
| Patients sortis | 1 (Bernard, amelioration) |
| Dossiers en attente | 2 (Petit — asthme, Garcia — entorse) |
| Demandes en attente | 2 (a traiter par le gestionnaire) |
| Mouvements de stock | 6 (sorties, entree, transfert, casse) |

---

## Structure de la base de donnees

21 tables principales :

| Domaine | Tables |
|---------|--------|
| Utilisateurs | `users`, `login_logs`, `journal_actions` |
| Patients | `patients`, `dossiers_prise_en_charge` |
| Hospitalisations | `chambres`, `hospitalisations` |
| Prescriptions | `ordonnances`, `lignes_ordonnances` |
| Produits | `produits`, `categories_produits`, `produits_fournisseurs`, `fournisseurs` |
| Stocks | `stock`, `emplacements_stock`, `mouvements_stock`, `demandes_produits` |
| Systeme | `alertes` |
