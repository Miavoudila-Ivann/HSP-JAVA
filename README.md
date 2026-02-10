# HSP-JAVA

Systeme de gestion hospitaliere developpe en Java 17 avec JavaFX.

## Fonctionnalites

- **Triage / Accueil** : creation de dossiers de prise en charge avec niveau de gravite (1 a 5)
- **Hospitalisation** : attribution de chambre avec verrouillage transactionnel, suivi du sejour, sortie patient
- **Ordonnances** : prescription medicamenteuse avec posologie, voie d'administration et duree
- **Gestion des stocks** : catalogue produits, emplacements multiples, lots, dates de peremption
- **Demandes de produits** : circuit medecin -> gestionnaire avec validation transactionnelle
- **Mouvements de stock** : entrees, sorties, transferts, casse avec tracabilite complete
- **Securite** : authentification BCrypt, controle d'acces par role (RBAC), verrouillage de compte
- **RGPD / Audit** : journalisation de toutes les actions (connexions, CRUD, exports)

## Roles utilisateurs

| Role | Acces |
|------|-------|
| `ADMIN` | Administration complete |
| `SECRETAIRE` | Accueil patients, creation dossiers triage |
| `MEDECIN` | Prise en charge, hospitalisations, ordonnances, demandes produits |
| `GESTIONNAIRE` | Gestion stocks, validation/refus demandes, reapprovisionnement |

## Prerequis

- **Java** 17+
- **Maven** 3.8+
- **MySQL** 8.0+

## Configuration de la base de donnees

### 1. Creer la base de donnees

```sql
CREATE DATABASE hsp_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Executer le schema

```bash
mysql -u root -p hsp_java < src/main/resources/sql/schema.sql
```

### 3. Charger les donnees de test

```bash
mysql -u root -p hsp_java < src/main/resources/sql/seed.sql
```

### 4. Parametres de connexion

La configuration se trouve dans `src/main/java/appli/util/DBConnection.java` :

```java
SERVEUR = "localhost"
NOM_BDD = "hsp_java"
UTILISATEUR = "root"
MOT_DE_PASSE = ""
```

Modifier ces valeurs selon votre environnement.

## Build et lancement

```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn javafx:run

# Lancer les tests
mvn test
```

## Comptes de test

Mot de passe pour tous les comptes : `password123`

| Email | Nom | Role |
|-------|-----|------|
| `admin@hsp.fr` | Jean Dupont | Administrateur |
| `secretaire1@hsp.fr` | Marie Martin | Secretaire |
| `secretaire2@hsp.fr` | Sophie Bernard | Secretaire |
| `medecin1@hsp.fr` | Pierre Leroy | Medecin (generaliste) |
| `medecin2@hsp.fr` | Claire Moreau | Medecin (urgences) |
| `medecin3@hsp.fr` | Francois Petit | Medecin (cardiologie) |
| `medecin4@hsp.fr` | Isabelle Roux | Medecin (pediatrie) |
| `gestionnaire1@hsp.fr` | Michel Fournier | Gestionnaire stock |
| `gestionnaire2@hsp.fr` | Anne Girard | Gestionnaire stock |

## Scenario demo (seed)

Le seed injecte un scenario complet sur plusieurs jours :

### Jour 1 (15 janvier)
1. **Secretaire** Marie Martin accueille 4 patients avec 4 niveaux de gravite differents
2. **Dr Leroy** prend en charge Dubois (douleur thoracique, gravite 4) :
   - Hospitalisation en soins intensifs chambre 301
   - Ordonnance : Aspirine + Adrenaline si besoin
   - Demande de seringues -> **validee** par le gestionnaire
3. Demande de gants latex -> **refusee** (rupture de stock)

### Jour 2 (16 janvier)
4. **Dr Petit** (cardiologue) prend en charge Moreau en urgence critique (IDM) :
   - Hospitalisation en reanimation chambre 303
   - Ordonnance avec **morphine** (stupefiant) + aspirine + adrenaline
   - Demande urgente de morphine -> **validee** depuis le coffre
5. **Dr Moreau** hospitalise Durand (fracture ouverte) chambre 104 pour chirurgie
6. **Dr Roux** hospitalise Faure (menace accouchement premature) en maternite chambre 403
7. **Dr Leroy** hospitalise Bernard (BPCO decompensee) chambre 201
8. Reapprovisionnement compresses + transfert adrenaline frigo -> urgences

### Jour 5 (21 janvier)
9. **Sortie de Bernard** avec amelioration : ordonnance relais PO, cloture du dossier

### Etat final
- **4 patients hospitalises** : Dubois (SI), Moreau (Rea), Durand (Chir), Faure (Mat)
- **1 patient sorti** : Bernard (amelioration, domicile)
- **2 dossiers en attente** : Petit (asthme), Garcia (entorse)
- **2 demandes en attente** a traiter par le gestionnaire
- **6 mouvements de stock** traces (sorties, entree, transfert, casse)

## Architecture

```
src/main/java/appli/
├── model/           17 entites (User, Patient, Hospitalisation, Stock...)
├── service/         Couche metier (MedicalService, StockService, AuthService...)
├── repository/      Interfaces + implementations JDBC
├── dao/             Acces donnees (legacy)
├── security/        Authentification, sessions, controle d'acces
├── ui/controller/   8 controleurs JavaFX
└── util/            DBConnection, Router, ValidationUtils

src/main/resources/
├── sql/             schema.sql + seed.sql
└── appli/view/      9 fichiers FXML

src/test/java/appli/
└── service/         Tests unitaires des services
```

## Tests

Les tests unitaires couvrent les regles metier des services :

- **MedicalServiceTest** : hospitalisation (controles securite, etats, capacite chambre), sortie patient
- **StockServiceTest** : validation demande de stock (securite, etats, livraison partielle), refus demande

```bash
mvn test
```
