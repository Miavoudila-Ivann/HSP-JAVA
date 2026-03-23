# Documentation Fonctionnelle - HSP-JAVA

## Plateforme de Gestion de Service d'Urgence Hospitalier

---

## 1. Presentation de l'application

HSP-JAVA est une application de bureau developpee en Java/JavaFX permettant de gerer un service d'urgence hospitalier. Elle couvre trois axes principaux :

- **Gestion des patients** : creation de fiches patients, ouverture de dossiers de prise en charge (triage), suivi des hospitalisations.
- **Gestion des stocks de produits medicaux** : suivi des produits, demandes par les medecins, validation/refus par le gestionnaire, reapprovisionnement aupres des fournisseurs.
- **Securite et tracabilite** : controle d'acces par role, double authentification (2FA), journalisation de toutes les actions (conformite RGPD).

---

## 2. Profils utilisateurs et permissions

L'application distingue **quatre profils** utilisateurs. Chaque utilisateur est identifie par son nom, prenom et email.

### 2.1 Administrateur (ADMIN)

Acces complet a toutes les fonctionnalites :
- Gestion des comptes utilisateurs (creation, modification, desactivation)
- Acces a toutes les vues de l'application
- Supervision du journal des actions et des connexions

### 2.2 Secretaire (SECRETAIRE)

Fonctionnalites accessibles :
- **Gestion des patients** : creation et modification des fiches patients
- **Triage** : enregistrement des dossiers de prise en charge (symptomes, niveau de gravite)
- **Gestion des dossiers** : consultation et mise a jour des dossiers
- **Rendez-vous** : planification des rendez-vous
- **Statistiques** : consultation des tableaux de bord

### 2.3 Medecin (MEDECIN)

Fonctionnalites accessibles :
- **Consultation des patients** : acces aux fiches patients
- **Gestion des dossiers** : prise en charge des dossiers en salle d'attente
- **Hospitalisations** : admission des patients dans les chambres, sortie des patients
- **Ordonnances** : creation et gestion des prescriptions medicales
- **Demandes de produits** : envoi de demandes au gestionnaire de stock
- **Rendez-vous** : gestion des rendez-vous
- **Export** : export de dossiers au format PDF
- **Statistiques** : consultation des tableaux de bord

### 2.4 Gestionnaire de stock (GESTIONNAIRE)

Fonctionnalites accessibles :
- **Gestion des produits** : creation, modification, suivi des produits medicaux
- **Validation des demandes** : acceptation ou refus des demandes de produits des medecins
- **Gestion des fournisseurs** : creation et gestion des fiches fournisseurs
- **Commandes fournisseurs** : creation de bons de commande, reception des livraisons
- **Export** : export de rapports au format PDF
- **Statistiques** : consultation des tableaux de bord

---

## 3. Guide d'utilisation par ecran

### 3.1 Ecran de connexion (Login)

**Acces** : Tous les utilisateurs

Au lancement de l'application, l'utilisateur saisit :
- **Email** : adresse email du compte
- **Mot de passe** : mot de passe securise

**Securite** :
- Apres **5 tentatives echouees**, le compte est verrouille pendant **30 minutes**
- Si la **double authentification (2FA)** est activee, un code TOTP a 6 chiffres est demande apres la saisie du mot de passe

### 3.2 Tableau de bord (Dashboard)

**Acces** : Tous les utilisateurs connectes

Ecran principal apres connexion. Affiche un menu de navigation adapte au role de l'utilisateur. Les boutons non autorises sont masques.

Bouton de configuration 2FA disponible pour activer/desactiver la double authentification.

### 3.3 Gestion des patients

**Acces** : Secretaire, Medecin (consultation), Admin

#### Creer une fiche patient
La secretaire remplit les champs suivants :
- **Nom** et **Prenom** (obligatoires)
- **Numero de securite sociale** (obligatoire, unique)
- **Date de naissance** et **Sexe**
- **Email**, **Telephone**, **Adresse**
- **Groupe sanguin**
- **Contact d'urgence** : nom, telephone, lien de parente
- **Medecin traitant**
- **Allergies connues** et **Antecedents medicaux**

Le systeme enregistre automatiquement l'identite de la secretaire ayant cree la fiche (`cree_par`).

#### Rechercher un patient
Une barre de recherche permet de filtrer les patients par nom dans le tableau.

#### Exporter une fiche patient
Le bouton d'export genere un fichier **PDF** contenant toutes les informations du patient.

### 3.4 Triage - Nouveau dossier de prise en charge

**Acces** : Secretaire, Admin

La secretaire enregistre un nouveau cas d'urgence :
- **Patient** : selection d'un patient existant
- **Motif d'admission** (obligatoire)
- **Symptomes** : description des symptomes observes
- **Niveau de gravite** (obligatoire, de 1 a 5) :

| Niveau | Libelle  | Couleur |
|--------|----------|---------|
| 1      | Mineur   | Vert    |
| 2      | Modere   | Vert clair |
| 3      | Serieux  | Orange  |
| 4      | Grave    | Orange fonce |
| 5      | Critique | Rouge   |

- **Mode d'arrivee** : Ambulance, Pompiers, Personnel, Transfert, Autre
- **Constantes vitales** : tension, pouls, temperature, saturation
- **Antecedents**, **Allergies**, **Traitement en cours**

Le systeme genere automatiquement :
- Un **numero de dossier** unique (format : `DPC-YYYYMMDD-XXXX`)
- La **date et heure d'arrivee**
- L'identification de la **secretaire** ayant cree le dossier

Le dossier passe en statut **EN_ATTENTE** dans la file de triage.

### 3.5 Detail du dossier

**Acces** : Secretaire (consultation), Medecin, Admin

Affiche le detail complet d'un dossier de prise en charge :
- Informations du patient
- Donnees cliniques (symptomes, constantes vitales)
- Ordonnances associees
- Hospitalisation associee (le cas echeant)

Actions disponibles pour le medecin :
- **Prescrire une ordonnance** (sortie avec traitement)
- **Hospitaliser le patient** (admission dans une chambre)
- **Cloturer le dossier** (sortie du patient)

### 3.6 Hospitalisations

**Acces** : Medecin, Admin

#### Hospitaliser un patient
Le medecin selectionne :
- Le **dossier** du patient a hospitaliser
- La **chambre** disponible (le systeme verifie automatiquement la disponibilite)
- Le **motif d'hospitalisation** et le **diagnostic d'entree**
- La **date de sortie prevue**

Le systeme :
- Verrouille la chambre pour eviter les conflits (transaction avec `SELECT FOR UPDATE`)
- Genere un **numero de sejour** (format : `SEJ-YYYYMMDD-XXXX`)
- Incremente le nombre de lits occupes dans la chambre
- Met a jour le statut du dossier a **HOSPITALISE**

#### Sortie d'un patient
Le medecin renseigne :
- Le **diagnostic de sortie**
- Le **type de sortie** : Guerison, Amelioration, Transfert, Contre avis medical, Deces, Autre
- Les **observations** finales

Le systeme libere automatiquement le lit dans la chambre.

### 3.7 Gestion des chambres

**Acces** : Admin

- **Types de chambres** : Simple, Double, Soins intensifs, Reanimation, Urgence, Pediatrie, Maternite
- Chaque chambre a un **numero**, un **etage**, un **batiment**, une **capacite** (nombre de lits)
- Suivi de l'**occupation** en temps reel (lits occupes / capacite)
- Possibilite de mettre une chambre en **maintenance**
- **Tarif journalier** configurable
- Liste des **equipements** disponibles

### 3.8 Ordonnances

**Acces** : Medecin, Admin

#### Creer une ordonnance
Le medecin cree une ordonnance liee a un dossier de prise en charge :
- **Notes** de prescription
- **Date de fin** prevue

Chaque ordonnance recoit un numero unique (format : `ORD-YYYYMMDD-XXXX`).

#### Ajouter des lignes de prescription
Pour chaque medicament prescrit :
- **Produit** : selection dans le catalogue
- **Posologie** : ex. "1 comprime matin et soir"
- **Quantite**
- **Duree** en jours
- **Frequence** : ex. "toutes les 8h"
- **Voie d'administration** : Orale, IV, IM, Sous-cutanee, Cutanee, Rectale, Inhalation, Autre
- **Instructions** specifiques

#### Terminer une ordonnance
Le medecin peut cloturer une ordonnance active.

### 3.9 Demandes de produits

**Acces** : Medecin (creation), Gestionnaire (validation/refus), Admin

#### Flux de travail

1. **Le medecin cree une demande** :
   - Selection du produit
   - Quantite demandee
   - Lien vers le dossier/hospitalisation concerne
   - Date de besoin souhaitee
   - Indicateur d'urgence
   - Motif de la demande

2. **Le gestionnaire de stock traite la demande** :
   - **Valider** : le stock est automatiquement decremente (transaction securisee). Si le stock est insuffisant, une livraison partielle est effectuee avec commentaire.
   - **Refuser** : le gestionnaire indique le motif du refus.

#### Statuts d'une demande
`EN_ATTENTE` -> `VALIDEE` -> `EN_PREPARATION` -> `PRETE` -> `LIVREE`
ou `EN_ATTENTE` -> `REFUSEE`
ou `EN_ATTENTE` -> `ANNULEE`

### 3.10 Gestion des stocks

**Acces** : Gestionnaire, Admin

#### Fiches produits
Chaque produit est defini par :
- **Code** unique et **Nom**
- **Description** et **Categorie**
- **Forme** : Comprime, Gelule, Sirop, Injectable, Pommade, Spray, Patch, Dispositif, Consommable, Autre
- **Dosage** : ex. "500mg", "10ml"
- **Unite de mesure**
- **Prix unitaire** et **TVA**
- **Niveau de dangerosite** (5 niveaux) :

| Niveau     | Couleur |
|------------|---------|
| Faible     | Vert    |
| Moyen      | Orange  |
| Eleve      | Orange fonce |
| Tres eleve | Rouge   |
| Critique   | Violet  |

- **Conditions de stockage** et **plage de temperature**
- **Seuil d'alerte stock** (defaut : 10 unites) : declenche une alerte quand le stock passe en dessous
- **Seuil de peremption** (defaut : 30 jours)
- Indicateurs : necessite ordonnance, stupefiant

#### Suivi des stocks
Pour chaque produit, le stock est suivi par :
- **Emplacement** (Pharmacie, Reserve, Urgence, Bloc, Service, Frigo, Coffre)
- **Lot** et **Date de peremption**
- **Quantite disponible** = quantite totale - quantite reservee

Indicateurs visuels :
- **RUPTURE** : stock a zero
- **ALERTE** : stock en dessous du seuil
- **PEREMPTION** : produit bientot perime
- **OK** : stock normal

#### Reapprovisionnement
Le gestionnaire enregistre les entrees de stock :
- Produit, emplacement, numero de lot
- Quantite recue
- Date de peremption
- Prix d'achat et fournisseur
- Numero de commande associe

### 3.11 Gestion des fournisseurs

**Acces** : Gestionnaire, Admin

Chaque fournisseur est defini par :
- **Code** unique et **Nom**
- **Raison sociale**, **SIRET**
- **Coordonnees** : adresse, telephone, email, site web
- **Contact** : nom, telephone, email du contact principal
- **Conditions de paiement** : ex. "30 jours fin de mois"
- **Delai de livraison** en jours
- **Note d'evaluation** sur 5

Un produit peut etre associe a **plusieurs fournisseurs** avec un prix d'achat specifique pour chacun.

### 3.12 Commandes fournisseurs

**Acces** : Gestionnaire, Admin

#### Creer une commande
1. Selectionner un **fournisseur**
2. Definir la **date de livraison prevue**
3. Ajouter des **lignes de commande** (produit, quantite, prix unitaire, TVA)
4. Le montant total HT/TTC est calcule automatiquement

#### Cycle de vie d'une commande
`BROUILLON` -> `ENVOYEE` -> `CONFIRMEE` -> `EN_LIVRAISON` -> `LIVREE` (ou `LIVREE_PARTIELLE`)
ou `ANNULEE`

#### Reception de livraison
A la reception, le gestionnaire indique les quantites recues pour chaque ligne. Le stock est automatiquement mis a jour.

### 3.13 Rendez-vous

**Acces** : Secretaire, Medecin, Admin

- Planification de rendez-vous entre un **patient** et un **medecin**
- **Types** : Consultation, Suivi, Examen, Chirurgie, Autre
- **Statuts** : Planifie, Confirme, Realise, Annule, Reporte
- Duree configurable (defaut : 30 minutes)
- Lieu et motif

### 3.14 Statistiques

**Acces** : Secretaire, Medecin, Gestionnaire, Admin

Le tableau de bord statistiques affiche :

**Indicateurs cles** :
- Nombre de patients hospitalises actuellement
- Nombre de dossiers en attente (salle d'attente)
- Nombre de produits en stock critique
- Taux d'occupation global des chambres

**Graphiques et analyses** :
- Hospitalisations par semaine (tendance sur 8 semaines)
- Taux d'occupation par type de chambre
- Repartition des niveaux de gravite
- Produits les plus demandes

**Export** : le rapport statistiques peut etre exporte en **PDF**.

### 3.15 Journal des actions

**Acces** : Admin

Affiche l'historique complet de toutes les actions effectuees dans l'application :
- **Type d'action** : Connexion, Deconnexion, Creation, Modification, Suppression, Consultation, Export, Echec de connexion
- **Utilisateur** ayant effectue l'action
- **Date et heure**
- **Description** de l'action
- **Entite concernee** (Patient, Produit, Ordonnance, etc.)
- **Adresse IP**

Filtres disponibles :
- Par utilisateur
- Par type d'action
- Par plage de dates

### 3.16 Journal des connexions

**Acces** : Admin

Affiche specifiquement l'historique des connexions et tentatives de connexion :
- Connexions reussies
- Echecs de connexion (identifiants invalides, compte verrouille, code 2FA invalide)

### 3.17 Gestion des utilisateurs

**Acces** : Admin uniquement

- **Creation de comptes** : seul l'administrateur peut creer de nouveaux comptes
- Champs : email, mot de passe, nom, prenom, role
- Le mot de passe doit respecter les regles de securite (voir section 4)
- **Modification** et **desactivation** de comptes

### 3.18 Configuration 2FA

**Acces** : Tous les utilisateurs connectes

Pour activer la double authentification :
1. L'utilisateur accede a la configuration 2FA depuis le tableau de bord
2. Un **QR code** est affiche a scanner avec une application d'authentification (Google Authenticator, Authy, Microsoft Authenticator)
3. L'utilisateur saisit le **code a 6 chiffres** affiche par l'application pour confirmer
4. La 2FA est activee. A chaque connexion suivante, le code sera demande.

---

## 4. Securite

### 4.1 Politique de mots de passe

Les mots de passe doivent contenir au minimum :
- **8 caracteres**
- **1 lettre majuscule**
- **1 lettre minuscule**
- **1 chiffre**
- **1 caractere special** parmi : `@ # $ % ^ & + = !`

### 4.2 Verrouillage de compte

- Apres **5 tentatives de connexion echouees**, le compte est automatiquement verrouille
- Le verrouillage dure **30 minutes**, apres quoi le compteur est reinitialise
- Les tentatives sont journalisees

### 4.3 Double authentification (2FA)

- Basee sur le protocole **TOTP** (RFC 6238)
- Codes a **6 chiffres** renouveles toutes les **30 secondes**
- Tolerance de **+/- 1 fenetre temporelle** (60 secondes au total)
- Compatible avec les applications standard (Google Authenticator, Authy, etc.)

### 4.4 Hachage des mots de passe

- Algorithme **BCrypt** avec un facteur de cout de **12**
- Salt genere automatiquement et integre au hash
- Protection contre les attaques par rainbow table

### 4.5 Controle d'acces

- **RBAC** (Role-Based Access Control) avec 18 permissions granulaires
- Verification des permissions avant chaque navigation
- Elements d'interface masques selon le role

---

## 5. Conformite RGPD

### 5.1 Tracabilite

- Chaque creation et modification de donnees est enregistree avec :
  - L'identite de l'utilisateur
  - La date et l'heure
  - L'adresse IP
  - Des snapshots **avant/apres** pour les modifications (champs JSON `donnees_avant` et `donnees_apres`)

### 5.2 Droit d'acces

- Export des fiches patients en PDF (droit d'acces aux donnees personnelles)
- Export des dossiers de prise en charge en PDF

### 5.3 Droit a l'effacement

- Politique de retention configurable pour les logs (methode `purgeOldLogs`)
- Suppression des anciens enregistrements de journalisation

### 5.4 Responsabilite

- Chaque fiche patient contient les champs `cree_par` et `modifie_par`
- Les ordonnances identifient le medecin prescripteur
- Les demandes de produits identifient le medecin demandeur et le gestionnaire traitant

---

## 6. Workflows metier

### 6.1 Parcours d'un patient aux urgences

```
Arrivee du patient
       |
       v
[Secretaire] Cree la fiche patient (si nouveau)
       |
       v
[Secretaire] Ouvre un dossier de prise en charge
             (symptomes, gravite 1-5)
       |
       v
Patient en salle d'attente (statut EN_ATTENTE)
       |
       v
[Medecin] Prend en charge le dossier (statut EN_COURS)
       |
       +---> Cas 1 : Ordonnance + Sortie
       |     [Medecin] Prescrit une ordonnance
       |     [Medecin] Cloture le dossier (destination DOMICILE)
       |     -> Le patient sort avec son ordonnance
       |
       +---> Cas 2 : Hospitalisation
             [Medecin] Selectionne une chambre disponible
             [Medecin] Cree l'hospitalisation
             -> Le patient est installe dans la chambre
             -> Le dossier passe en statut HOSPITALISE
                    |
                    v
             [Medecin] Suivi quotidien (observations, evolution)
                    |
                    v
             [Medecin] Sortie du patient
             -> Le lit est libere dans la chambre
             -> Le dossier passe en statut TERMINE
```

### 6.2 Circuit d'une demande de produit

```
[Medecin] Cree une demande de produit
          (produit, quantite, urgence, motif)
              |
              v
    Statut : EN_ATTENTE
              |
              v
[Gestionnaire] Consulte les demandes en attente
              |
     +--------+--------+
     |                  |
     v                  v
  VALIDER            REFUSER
     |                  |
     v                  v
  Le stock est       Le gestionnaire
  decremente         indique le motif
  automatiquement    du refus
     |
     v
  Statut : VALIDEE
  (livraison partielle si stock insuffisant)
```

### 6.3 Cycle de reapprovisionnement

```
[Systeme] Alerte stock bas detectee
              |
              v
[Gestionnaire] Cree une commande fournisseur (BROUILLON)
              |
              v
[Gestionnaire] Ajoute les lignes (produits, quantites, prix)
              |
              v
[Gestionnaire] Envoie la commande (ENVOYEE)
              |
              v
[Fournisseur] Confirme et livre
              |
              v
[Gestionnaire] Recoit la livraison
              |
              v
    Stock automatiquement mis a jour
    Mouvement d'ENTREE enregistre
```

---

## 7. Notifications et alertes

Le systeme genere des alertes automatiques pour :

| Type d'alerte       | Declenchement                              | Niveau    |
|---------------------|--------------------------------------------|-----------|
| Stock bas           | Quantite sous le seuil d'alerte            | WARNING   |
| Rupture de stock    | Quantite a zero                            | CRITICAL  |
| Peremption proche   | Produit expirant sous le seuil de jours    | WARNING   |
| Demande urgente     | Demande marquee comme urgente              | WARNING   |
| Commande en retard  | Livraison depassant la date prevue         | WARNING   |
| Temperature         | Deviation de temperature d'un emplacement  | CRITICAL  |

---

## 8. Exports PDF

L'application permet d'exporter trois types de documents en PDF :

1. **Fiche patient** : informations personnelles, coordonnees, contact d'urgence, informations medicales
2. **Dossier de prise en charge** : informations patient, donnees cliniques, ordonnances avec lignes de prescription, hospitalisation, cloture
3. **Rapport statistiques** : indicateurs cles, hospitalisations par semaine, repartition par gravite, produits les plus demandes, taux d'occupation par type de chambre
