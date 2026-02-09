-- ============================================================
-- HSP-JAVA - Schema de base de donnees
-- Base: hsp_java | Moteur: InnoDB | Charset: utf8mb4
-- ============================================================

DROP DATABASE IF EXISTS hsp_java;
CREATE DATABASE hsp_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hsp_java;

-- ============================================================
-- 1. UTILISATEURS ET AUTHENTIFICATION
-- ============================================================

-- Table des utilisateurs
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'SECRETAIRE', 'MEDECIN', 'GESTIONNAIRE') NOT NULL,
    specialite VARCHAR(100) NULL COMMENT 'Specialite medicale (pour MEDECIN)',
    telephone VARCHAR(20) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME NULL,
    tentatives_connexion INT NOT NULL DEFAULT 0,
    compte_verrouille BOOLEAN NOT NULL DEFAULT FALSE,
    date_verrouillage DATETIME NULL,

    CONSTRAINT uk_users_email UNIQUE (email),
    INDEX idx_users_role (role),
    INDEX idx_users_actif (actif),
    INDEX idx_users_nom_prenom (nom, prenom)
) ENGINE=InnoDB;

-- Table du journal des actions (RGPD / Audit)
CREATE TABLE journal_actions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    type_action ENUM('CONNEXION', 'DECONNEXION', 'CREATION', 'MODIFICATION', 'SUPPRESSION', 'CONSULTATION', 'EXPORT', 'ECHEC_CONNEXION') NOT NULL,
    description TEXT NOT NULL,
    date_action DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresse_ip VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    entite VARCHAR(100) NULL COMMENT 'Nom de la table concernee',
    entite_id INT NULL COMMENT 'ID de l enregistrement concerne',
    donnees_avant JSON NULL COMMENT 'Snapshot avant modification',
    donnees_apres JSON NULL COMMENT 'Snapshot apres modification',

    INDEX idx_journal_user (user_id),
    INDEX idx_journal_date (date_action),
    INDEX idx_journal_type (type_action),
    INDEX idx_journal_entite (entite, entite_id),
    CONSTRAINT fk_journal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 2. PATIENTS
-- ============================================================

CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_securite_sociale VARCHAR(15) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    sexe ENUM('M', 'F') NOT NULL,
    groupe_sanguin ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NULL,
    adresse TEXT NULL,
    code_postal VARCHAR(10) NULL,
    ville VARCHAR(100) NULL,
    telephone VARCHAR(20) NULL,
    telephone_mobile VARCHAR(20) NULL,
    email VARCHAR(255) NULL,
    personne_contact_nom VARCHAR(200) NULL,
    personne_contact_telephone VARCHAR(20) NULL,
    personne_contact_lien VARCHAR(50) NULL COMMENT 'Lien de parente',
    medecin_traitant VARCHAR(200) NULL,
    notes_medicales TEXT NULL,
    allergies_connues TEXT NULL,
    antecedents_medicaux TEXT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    cree_par INT NULL,
    modifie_par INT NULL,

    CONSTRAINT uk_patients_secu UNIQUE (numero_securite_sociale),
    INDEX idx_patients_nom (nom, prenom),
    INDEX idx_patients_ville (ville),
    INDEX idx_patients_date_naissance (date_naissance),
    CONSTRAINT fk_patients_cree_par FOREIGN KEY (cree_par) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_patients_modifie_par FOREIGN KEY (modifie_par) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 3. DOSSIERS DE PRISE EN CHARGE (TRIAGE)
-- ============================================================

CREATE TABLE dossiers_prise_en_charge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_dossier VARCHAR(20) NOT NULL COMMENT 'Format: DPC-YYYYMMDD-XXXX',
    patient_id INT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_admission DATETIME NULL,
    motif_admission TEXT NOT NULL,
    niveau_gravite ENUM('NIVEAU_1', 'NIVEAU_2', 'NIVEAU_3', 'NIVEAU_4', 'NIVEAU_5') NOT NULL
        COMMENT '1=Mineur, 2=Modere, 3=Serieux, 4=Grave, 5=Critique',
    mode_arrivee ENUM('AMBULANCE', 'POMPIERS', 'PERSONNEL', 'TRANSFERT', 'AUTRE') NULL,
    symptomes TEXT NULL,
    constantes_vitales JSON NULL COMMENT 'tension, pouls, temperature, saturation, etc.',
    antecedents TEXT NULL,
    allergies TEXT NULL,
    traitement_en_cours TEXT NULL,
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'HOSPITALISE', 'TERMINE', 'TRANSFERE', 'ANNULE', 'DECEDE') NOT NULL DEFAULT 'EN_ATTENTE',
    priorite_triage INT NOT NULL DEFAULT 0 COMMENT 'Score de priorite calcule',
    medecin_responsable_id INT NULL,
    cree_par INT NULL,
    date_prise_en_charge DATETIME NULL,
    date_cloture DATETIME NULL,
    notes_cloture TEXT NULL,
    destination_sortie ENUM('DOMICILE', 'HOSPITALISATION', 'TRANSFERT', 'DECES', 'AUTRE') NULL,

    CONSTRAINT uk_dossiers_numero UNIQUE (numero_dossier),
    INDEX idx_dossiers_patient (patient_id),
    INDEX idx_dossiers_statut (statut),
    INDEX idx_dossiers_gravite (niveau_gravite),
    INDEX idx_dossiers_date (date_creation),
    INDEX idx_dossiers_priorite (priorite_triage DESC, date_creation ASC),
    CONSTRAINT fk_dossiers_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_dossiers_medecin FOREIGN KEY (medecin_responsable_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_dossiers_cree_par FOREIGN KEY (cree_par) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 4. CHAMBRES ET HOSPITALISATIONS
-- ============================================================

CREATE TABLE chambres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    etage INT NOT NULL,
    batiment VARCHAR(50) NULL DEFAULT 'Principal',
    type_chambre ENUM('SIMPLE', 'DOUBLE', 'SOINS_INTENSIFS', 'REANIMATION', 'URGENCE', 'PEDIATRIE', 'MATERNITE') NOT NULL,
    capacite INT NOT NULL DEFAULT 1,
    nb_lits_occupes INT NOT NULL DEFAULT 0,
    equipements TEXT NULL COMMENT 'Liste des equipements disponibles',
    tarif_journalier DECIMAL(10,2) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    en_maintenance BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT NULL,

    CONSTRAINT uk_chambres_numero UNIQUE (numero),
    INDEX idx_chambres_type (type_chambre),
    INDEX idx_chambres_etage (etage),
    INDEX idx_chambres_disponible (actif, nb_lits_occupes, capacite),
    CONSTRAINT chk_chambres_occupation CHECK (nb_lits_occupes <= capacite AND nb_lits_occupes >= 0)
) ENGINE=InnoDB;

CREATE TABLE hospitalisations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_sejour VARCHAR(20) NOT NULL COMMENT 'Format: SEJ-YYYYMMDD-XXXX',
    dossier_id INT NOT NULL,
    chambre_id INT NOT NULL,
    lit_numero INT NULL COMMENT 'Numero du lit dans la chambre',
    date_entree DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_sortie_prevue DATE NULL,
    date_sortie_effective DATETIME NULL,
    motif_hospitalisation TEXT NOT NULL,
    diagnostic_entree TEXT NULL,
    diagnostic_sortie TEXT NULL,
    traitement TEXT NULL,
    observations TEXT NULL,
    evolution TEXT NULL COMMENT 'Notes d evolution quotidiennes',
    statut ENUM('EN_COURS', 'TERMINEE', 'TRANSFEREE', 'DECES') NOT NULL DEFAULT 'EN_COURS',
    type_sortie ENUM('GUERISON', 'AMELIORATION', 'TRANSFERT', 'CONTRE_AVIS', 'DECES', 'AUTRE') NULL,
    medecin_id INT NOT NULL,
    medecin_sortie_id INT NULL,

    CONSTRAINT uk_hospitalisations_numero UNIQUE (numero_sejour),
    INDEX idx_hospi_dossier (dossier_id),
    INDEX idx_hospi_chambre (chambre_id),
    INDEX idx_hospi_statut (statut),
    INDEX idx_hospi_dates (date_entree, date_sortie_effective),
    INDEX idx_hospi_medecin (medecin_id),
    CONSTRAINT fk_hospi_dossier FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE RESTRICT,
    CONSTRAINT fk_hospi_chambre FOREIGN KEY (chambre_id) REFERENCES chambres(id) ON DELETE RESTRICT,
    CONSTRAINT fk_hospi_medecin FOREIGN KEY (medecin_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_hospi_medecin_sortie FOREIGN KEY (medecin_sortie_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 5. ORDONNANCES ET PRESCRIPTIONS
-- ============================================================

CREATE TABLE ordonnances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_ordonnance VARCHAR(20) NOT NULL COMMENT 'Format: ORD-YYYYMMDD-XXXX',
    dossier_id INT NOT NULL,
    hospitalisation_id INT NULL,
    medecin_id INT NOT NULL,
    date_prescription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_debut DATE NOT NULL,
    date_fin DATE NULL,
    statut ENUM('ACTIVE', 'TERMINEE', 'ANNULEE', 'SUSPENDUE') NOT NULL DEFAULT 'ACTIVE',
    notes TEXT NULL,

    CONSTRAINT uk_ordonnances_numero UNIQUE (numero_ordonnance),
    INDEX idx_ordonnances_dossier (dossier_id),
    INDEX idx_ordonnances_hospi (hospitalisation_id),
    INDEX idx_ordonnances_medecin (medecin_id),
    INDEX idx_ordonnances_statut (statut),
    INDEX idx_ordonnances_dates (date_debut, date_fin),
    CONSTRAINT fk_ordonnances_dossier FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ordonnances_hospi FOREIGN KEY (hospitalisation_id) REFERENCES hospitalisations(id) ON DELETE SET NULL,
    CONSTRAINT fk_ordonnances_medecin FOREIGN KEY (medecin_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE lignes_ordonnance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ordonnance_id INT NOT NULL,
    produit_id INT NOT NULL,
    posologie VARCHAR(255) NOT NULL COMMENT 'Ex: 1 comprime matin et soir',
    quantite INT NOT NULL,
    duree_jours INT NULL,
    frequence VARCHAR(100) NULL COMMENT 'Ex: toutes les 8h',
    voie_administration ENUM('ORALE', 'IV', 'IM', 'SC', 'CUTANEE', 'RECTALE', 'INHALATION', 'AUTRE') NOT NULL DEFAULT 'ORALE',
    instructions TEXT NULL,
    date_debut DATE NULL,
    date_fin DATE NULL,

    INDEX idx_lignes_ordonnance (ordonnance_id),
    INDEX idx_lignes_produit (produit_id),
    CONSTRAINT fk_lignes_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnances(id) ON DELETE CASCADE,
    CONSTRAINT fk_lignes_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 6. FOURNISSEURS
-- ============================================================

CREATE TABLE fournisseurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    nom VARCHAR(200) NOT NULL,
    raison_sociale VARCHAR(255) NULL,
    siret VARCHAR(14) NULL,
    adresse TEXT NULL,
    code_postal VARCHAR(10) NULL,
    ville VARCHAR(100) NULL,
    pays VARCHAR(100) NULL DEFAULT 'France',
    telephone VARCHAR(20) NULL,
    fax VARCHAR(20) NULL,
    email VARCHAR(255) NULL,
    site_web VARCHAR(255) NULL,
    contact_nom VARCHAR(200) NULL,
    contact_telephone VARCHAR(20) NULL,
    contact_email VARCHAR(255) NULL,
    conditions_paiement VARCHAR(100) NULL COMMENT 'Ex: 30 jours fin de mois',
    delai_livraison_jours INT NULL,
    note_evaluation DECIMAL(3,2) NULL COMMENT 'Note sur 5',
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_fournisseurs_code UNIQUE (code),
    INDEX idx_fournisseurs_nom (nom),
    INDEX idx_fournisseurs_ville (ville),
    INDEX idx_fournisseurs_actif (actif)
) ENGINE=InnoDB;

-- ============================================================
-- 7. PRODUITS ET CATEGORIES
-- ============================================================

CREATE TABLE categories_produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    description TEXT NULL,
    categorie_parent_id INT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_categories_code UNIQUE (code),
    INDEX idx_categories_parent (categorie_parent_id),
    CONSTRAINT fk_categories_parent FOREIGN KEY (categorie_parent_id) REFERENCES categories_produits(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    code_cip VARCHAR(13) NULL COMMENT 'Code CIP pour medicaments',
    nom VARCHAR(200) NOT NULL,
    nom_commercial VARCHAR(200) NULL,
    description TEXT NULL,
    categorie_id INT NULL,
    forme ENUM('COMPRIME', 'GELULE', 'SIROP', 'INJECTABLE', 'POMMADE', 'SPRAY', 'PATCH', 'DISPOSITIF', 'CONSOMMABLE', 'AUTRE') NULL,
    dosage VARCHAR(100) NULL COMMENT 'Ex: 500mg, 10ml',
    unite_mesure VARCHAR(50) NOT NULL,
    prix_unitaire DECIMAL(10,2) NULL,
    tva DECIMAL(5,2) NULL DEFAULT 20.00,
    niveau_dangerosite ENUM('FAIBLE', 'MOYEN', 'ELEVE', 'TRES_ELEVE') NOT NULL DEFAULT 'FAIBLE',
    conditions_stockage TEXT NULL,
    temperature_min DECIMAL(5,2) NULL,
    temperature_max DECIMAL(5,2) NULL,
    necessite_ordonnance BOOLEAN NOT NULL DEFAULT FALSE,
    stupefiant BOOLEAN NOT NULL DEFAULT FALSE,
    date_peremption_alerte_jours INT NOT NULL DEFAULT 30,
    seuil_alerte_stock INT NOT NULL DEFAULT 10,
    seuil_commande_auto INT NULL COMMENT 'Quantite declenchant commande auto',
    fournisseur_principal_id INT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_produits_code UNIQUE (code),
    INDEX idx_produits_nom (nom),
    INDEX idx_produits_categorie (categorie_id),
    INDEX idx_produits_fournisseur (fournisseur_principal_id),
    INDEX idx_produits_dangerosite (niveau_dangerosite),
    INDEX idx_produits_actif (actif),
    CONSTRAINT fk_produits_categorie FOREIGN KEY (categorie_id) REFERENCES categories_produits(id) ON DELETE SET NULL,
    CONSTRAINT fk_produits_fournisseur FOREIGN KEY (fournisseur_principal_id) REFERENCES fournisseurs(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Association produits-fournisseurs (un produit peut avoir plusieurs fournisseurs)
CREATE TABLE produits_fournisseurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    fournisseur_id INT NOT NULL,
    reference_fournisseur VARCHAR(50) NULL,
    prix_achat DECIMAL(10,2) NULL,
    delai_livraison_jours INT NULL,
    quantite_minimum_commande INT NULL,
    est_principal BOOLEAN NOT NULL DEFAULT FALSE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_produit_fournisseur UNIQUE (produit_id, fournisseur_id),
    INDEX idx_pf_produit (produit_id),
    INDEX idx_pf_fournisseur (fournisseur_id),
    CONSTRAINT fk_pf_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE,
    CONSTRAINT fk_pf_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 8. STOCKS
-- ============================================================

CREATE TABLE emplacements_stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    description TEXT NULL,
    type_emplacement ENUM('PHARMACIE', 'RESERVE', 'URGENCE', 'BLOC', 'SERVICE', 'FRIGO', 'COFFRE') NOT NULL,
    temperature_controlee BOOLEAN NOT NULL DEFAULT FALSE,
    temperature_cible DECIMAL(5,2) NULL,
    responsable_id INT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_emplacements_code UNIQUE (code),
    INDEX idx_emplacements_type (type_emplacement),
    CONSTRAINT fk_emplacements_responsable FOREIGN KEY (responsable_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE stocks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    emplacement_id INT NOT NULL,
    lot VARCHAR(50) NOT NULL,
    quantite INT NOT NULL DEFAULT 0,
    quantite_reservee INT NOT NULL DEFAULT 0 COMMENT 'Quantite reservee pour demandes en attente',
    date_peremption DATE NULL,
    date_reception DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prix_unitaire_achat DECIMAL(10,2) NULL,
    fournisseur_id INT NULL,
    numero_commande VARCHAR(50) NULL,
    date_derniere_maj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_stocks_produit (produit_id),
    INDEX idx_stocks_emplacement (emplacement_id),
    INDEX idx_stocks_lot (lot),
    INDEX idx_stocks_peremption (date_peremption),
    INDEX idx_stocks_quantite (quantite),
    CONSTRAINT fk_stocks_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stocks_emplacement FOREIGN KEY (emplacement_id) REFERENCES emplacements_stock(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stocks_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id) ON DELETE SET NULL,
    CONSTRAINT chk_stocks_quantite CHECK (quantite >= 0),
    CONSTRAINT chk_stocks_reserve CHECK (quantite_reservee >= 0 AND quantite_reservee <= quantite)
) ENGINE=InnoDB;

-- ============================================================
-- 9. MOUVEMENTS DE STOCK
-- ============================================================

CREATE TABLE mouvements_stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    stock_id INT NOT NULL,
    produit_id INT NOT NULL,
    type_mouvement ENUM('ENTREE', 'SORTIE', 'TRANSFERT', 'AJUSTEMENT', 'PEREMPTION', 'CASSE', 'RETOUR') NOT NULL,
    quantite INT NOT NULL,
    quantite_avant INT NOT NULL,
    quantite_apres INT NOT NULL,
    motif TEXT NULL,
    reference_document VARCHAR(100) NULL COMMENT 'Numero bon livraison, ordonnance, etc.',
    emplacement_source_id INT NULL,
    emplacement_destination_id INT NULL,
    dossier_id INT NULL COMMENT 'Lien vers dossier patient si sortie pour soin',
    ordonnance_id INT NULL,
    user_id INT NOT NULL,
    date_mouvement DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valide BOOLEAN NOT NULL DEFAULT TRUE,
    date_validation DATETIME NULL,
    validateur_id INT NULL,

    INDEX idx_mouvements_stock (stock_id),
    INDEX idx_mouvements_produit (produit_id),
    INDEX idx_mouvements_type (type_mouvement),
    INDEX idx_mouvements_date (date_mouvement),
    INDEX idx_mouvements_user (user_id),
    INDEX idx_mouvements_dossier (dossier_id),
    CONSTRAINT fk_mouvements_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mouvements_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mouvements_source FOREIGN KEY (emplacement_source_id) REFERENCES emplacements_stock(id) ON DELETE SET NULL,
    CONSTRAINT fk_mouvements_dest FOREIGN KEY (emplacement_destination_id) REFERENCES emplacements_stock(id) ON DELETE SET NULL,
    CONSTRAINT fk_mouvements_dossier FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE SET NULL,
    CONSTRAINT fk_mouvements_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnances(id) ON DELETE SET NULL,
    CONSTRAINT fk_mouvements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mouvements_validateur FOREIGN KEY (validateur_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 10. DEMANDES DE PRODUITS
-- ============================================================

CREATE TABLE demandes_produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_demande VARCHAR(20) NOT NULL COMMENT 'Format: DEM-YYYYMMDD-XXXX',
    produit_id INT NOT NULL,
    quantite_demandee INT NOT NULL,
    quantite_livree INT NULL DEFAULT 0,
    medecin_id INT NOT NULL,
    dossier_id INT NULL,
    hospitalisation_id INT NULL,
    ordonnance_id INT NULL,
    emplacement_destination_id INT NULL,
    date_demande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_besoin DATE NULL COMMENT 'Date souhaitee de livraison',
    urgence BOOLEAN NOT NULL DEFAULT FALSE,
    priorite INT NOT NULL DEFAULT 0 COMMENT '0=normale, 1=haute, 2=urgente',
    motif TEXT NULL,
    statut ENUM('EN_ATTENTE', 'VALIDEE', 'EN_PREPARATION', 'PRETE', 'LIVREE', 'REFUSEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    gestionnaire_id INT NULL,
    date_traitement DATETIME NULL,
    commentaire_traitement TEXT NULL,
    date_livraison DATETIME NULL,
    livreur_id INT NULL,

    CONSTRAINT uk_demandes_numero UNIQUE (numero_demande),
    INDEX idx_demandes_produit (produit_id),
    INDEX idx_demandes_statut (statut),
    INDEX idx_demandes_urgence (urgence, priorite DESC),
    INDEX idx_demandes_date (date_demande),
    INDEX idx_demandes_medecin (medecin_id),
    INDEX idx_demandes_dossier (dossier_id),
    CONSTRAINT fk_demandes_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT,
    CONSTRAINT fk_demandes_medecin FOREIGN KEY (medecin_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_demandes_dossier FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE SET NULL,
    CONSTRAINT fk_demandes_hospi FOREIGN KEY (hospitalisation_id) REFERENCES hospitalisations(id) ON DELETE SET NULL,
    CONSTRAINT fk_demandes_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnances(id) ON DELETE SET NULL,
    CONSTRAINT fk_demandes_emplacement FOREIGN KEY (emplacement_destination_id) REFERENCES emplacements_stock(id) ON DELETE SET NULL,
    CONSTRAINT fk_demandes_gestionnaire FOREIGN KEY (gestionnaire_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_demandes_livreur FOREIGN KEY (livreur_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 11. COMMANDES FOURNISSEURS
-- ============================================================

CREATE TABLE commandes_fournisseurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_commande VARCHAR(20) NOT NULL COMMENT 'Format: CMD-YYYYMMDD-XXXX',
    fournisseur_id INT NOT NULL,
    date_commande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_livraison_prevue DATE NULL,
    date_livraison_effective DATETIME NULL,
    statut ENUM('BROUILLON', 'ENVOYEE', 'CONFIRMEE', 'EN_LIVRAISON', 'LIVREE_PARTIELLE', 'LIVREE', 'ANNULEE') NOT NULL DEFAULT 'BROUILLON',
    montant_ht DECIMAL(12,2) NULL,
    montant_tva DECIMAL(12,2) NULL,
    montant_ttc DECIMAL(12,2) NULL,
    notes TEXT NULL,
    createur_id INT NOT NULL,
    validateur_id INT NULL,
    date_validation DATETIME NULL,

    CONSTRAINT uk_commandes_numero UNIQUE (numero_commande),
    INDEX idx_commandes_fournisseur (fournisseur_id),
    INDEX idx_commandes_statut (statut),
    INDEX idx_commandes_date (date_commande),
    CONSTRAINT fk_commandes_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id) ON DELETE RESTRICT,
    CONSTRAINT fk_commandes_createur FOREIGN KEY (createur_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_commandes_validateur FOREIGN KEY (validateur_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE lignes_commande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    commande_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite_commandee INT NOT NULL,
    quantite_recue INT NOT NULL DEFAULT 0,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    tva DECIMAL(5,2) NOT NULL DEFAULT 20.00,
    remise_pourcent DECIMAL(5,2) NULL DEFAULT 0,
    montant_ht DECIMAL(10,2) NOT NULL,

    INDEX idx_lignes_cmd_commande (commande_id),
    INDEX idx_lignes_cmd_produit (produit_id),
    CONSTRAINT fk_lignes_cmd_commande FOREIGN KEY (commande_id) REFERENCES commandes_fournisseurs(id) ON DELETE CASCADE,
    CONSTRAINT fk_lignes_cmd_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 12. ALERTES
-- ============================================================

CREATE TABLE alertes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_alerte ENUM('STOCK_BAS', 'PEREMPTION', 'RUPTURE', 'DEMANDE_URGENTE', 'COMMANDE_RETARD', 'TEMPERATURE', 'AUTRE') NOT NULL,
    niveau ENUM('INFO', 'WARNING', 'CRITICAL') NOT NULL DEFAULT 'WARNING',
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    entite VARCHAR(100) NULL,
    entite_id INT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_lecture DATETIME NULL,
    lu_par INT NULL,
    date_resolution DATETIME NULL,
    resolu_par INT NULL,
    notes_resolution TEXT NULL,

    INDEX idx_alertes_type (type_alerte),
    INDEX idx_alertes_niveau (niveau),
    INDEX idx_alertes_date (date_creation),
    INDEX idx_alertes_non_lu (date_lecture),
    CONSTRAINT fk_alertes_lu_par FOREIGN KEY (lu_par) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_alertes_resolu_par FOREIGN KEY (resolu_par) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 13. VUES UTILES
-- ============================================================

-- Vue des stocks avec details produit et alerte
CREATE OR REPLACE VIEW v_stocks_details AS
SELECT
    s.id AS stock_id,
    s.produit_id,
    p.code AS produit_code,
    p.nom AS produit_nom,
    p.niveau_dangerosite,
    s.emplacement_id,
    e.nom AS emplacement_nom,
    e.type_emplacement,
    s.lot,
    s.quantite,
    s.quantite_reservee,
    (s.quantite - s.quantite_reservee) AS quantite_disponible,
    s.date_peremption,
    DATEDIFF(s.date_peremption, CURDATE()) AS jours_avant_peremption,
    p.seuil_alerte_stock,
    CASE
        WHEN s.quantite <= 0 THEN 'RUPTURE'
        WHEN s.quantite <= p.seuil_alerte_stock THEN 'ALERTE'
        WHEN s.date_peremption <= DATE_ADD(CURDATE(), INTERVAL p.date_peremption_alerte_jours DAY) THEN 'PEREMPTION'
        ELSE 'OK'
    END AS statut_stock
FROM stocks s
JOIN produits p ON s.produit_id = p.id
JOIN emplacements_stock e ON s.emplacement_id = e.id
WHERE p.actif = TRUE;

-- Vue des dossiers en attente de triage
CREATE OR REPLACE VIEW v_dossiers_triage AS
SELECT
    d.id AS dossier_id,
    d.numero_dossier,
    d.date_creation,
    TIMESTAMPDIFF(MINUTE, d.date_creation, NOW()) AS attente_minutes,
    d.niveau_gravite,
    d.priorite_triage,
    d.motif_admission,
    d.statut,
    p.id AS patient_id,
    p.nom AS patient_nom,
    p.prenom AS patient_prenom,
    TIMESTAMPDIFF(YEAR, p.date_naissance, CURDATE()) AS patient_age,
    p.sexe AS patient_sexe,
    u.nom AS medecin_nom,
    u.prenom AS medecin_prenom
FROM dossiers_prise_en_charge d
JOIN patients p ON d.patient_id = p.id
LEFT JOIN users u ON d.medecin_responsable_id = u.id
WHERE d.statut IN ('EN_ATTENTE', 'EN_COURS')
ORDER BY
    d.niveau_gravite DESC,
    d.priorite_triage DESC,
    d.date_creation ASC;

-- Vue occupation des chambres
CREATE OR REPLACE VIEW v_occupation_chambres AS
SELECT
    c.id AS chambre_id,
    c.numero,
    c.etage,
    c.batiment,
    c.type_chambre,
    c.capacite,
    c.nb_lits_occupes,
    (c.capacite - c.nb_lits_occupes) AS lits_disponibles,
    ROUND((c.nb_lits_occupes / c.capacite) * 100, 1) AS taux_occupation,
    c.en_maintenance,
    c.actif
FROM chambres c
WHERE c.actif = TRUE;

-- ============================================================
-- FIN DU SCHEMA
-- ============================================================
