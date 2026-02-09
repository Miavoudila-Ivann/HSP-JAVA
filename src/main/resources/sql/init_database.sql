-- Script d'initialisation de la base de donnees HSP-JAVA
-- Base de donnees: hsp_java

CREATE DATABASE IF NOT EXISTS hsp_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hsp_java;

-- =====================================================
-- Table des utilisateurs
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'SECRETAIRE', 'MEDECIN', 'GESTIONNAIRE') NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME NULL,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- =====================================================
-- Table des patients
-- =====================================================
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_securite_sociale VARCHAR(15) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    sexe ENUM('M', 'F') NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20),
    email VARCHAR(255),
    personne_contact_nom VARCHAR(200),
    personne_contact_telephone VARCHAR(20),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    cree_par INT NULL,
    INDEX idx_patients_nom (nom, prenom),
    INDEX idx_patients_secu (numero_securite_sociale),
    FOREIGN KEY (cree_par) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- Table des chambres
-- =====================================================
CREATE TABLE IF NOT EXISTS chambres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    etage INT NOT NULL,
    type_chambre ENUM('SIMPLE', 'DOUBLE', 'SOINS_INTENSIFS', 'URGENCE') NOT NULL,
    capacite INT NOT NULL DEFAULT 1,
    occupee BOOLEAN DEFAULT FALSE,
    INDEX idx_chambres_numero (numero),
    INDEX idx_chambres_type (type_chambre),
    INDEX idx_chambres_disponible (occupee)
) ENGINE=InnoDB;

-- =====================================================
-- Table des produits
-- =====================================================
CREATE TABLE IF NOT EXISTS produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    categorie VARCHAR(100),
    unite_mesure VARCHAR(50),
    niveau_dangerosite ENUM('FAIBLE', 'MOYEN', 'ELEVE', 'TRES_ELEVE') DEFAULT 'FAIBLE',
    conditions_stockage TEXT,
    date_peremption_alerte_jours INT DEFAULT 30,
    seuil_alerte_stock INT DEFAULT 10,
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_produits_code (code),
    INDEX idx_produits_categorie (categorie)
) ENGINE=InnoDB;

-- =====================================================
-- Table des stocks
-- =====================================================
CREATE TABLE IF NOT EXISTS stocks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    quantite INT NOT NULL DEFAULT 0,
    emplacement VARCHAR(100),
    lot VARCHAR(50),
    date_peremption DATE,
    date_derniere_maj DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stocks_produit (produit_id),
    INDEX idx_stocks_peremption (date_peremption),
    FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- Table des fournisseurs
-- =====================================================
CREATE TABLE IF NOT EXISTS fournisseurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20),
    email VARCHAR(255),
    contact_nom VARCHAR(200),
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fournisseurs_nom (nom)
) ENGINE=InnoDB;

-- =====================================================
-- Table des dossiers de prise en charge
-- =====================================================
CREATE TABLE IF NOT EXISTS dossiers_prise_en_charge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    motif_admission TEXT NOT NULL,
    niveau_gravite ENUM('NIVEAU_1', 'NIVEAU_2', 'NIVEAU_3', 'NIVEAU_4', 'NIVEAU_5') NOT NULL,
    symptomes TEXT,
    antecedents TEXT,
    allergies TEXT,
    traitement_en_cours TEXT,
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'TERMINE', 'ANNULE') DEFAULT 'EN_ATTENTE',
    medecin_responsable_id INT NULL,
    cree_par INT NULL,
    date_cloture DATETIME NULL,
    notes_cloture TEXT,
    INDEX idx_dossiers_patient (patient_id),
    INDEX idx_dossiers_statut (statut),
    INDEX idx_dossiers_gravite (niveau_gravite),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_responsable_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (cree_par) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- Table des hospitalisations
-- =====================================================
CREATE TABLE IF NOT EXISTS hospitalisations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dossier_id INT NOT NULL,
    chambre_id INT NOT NULL,
    date_entree DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_sortie_prevue DATE,
    date_sortie_effective DATETIME NULL,
    motif_hospitalisation TEXT,
    diagnostic TEXT,
    traitement TEXT,
    observations TEXT,
    statut ENUM('EN_COURS', 'TERMINEE', 'TRANSFEREE') DEFAULT 'EN_COURS',
    medecin_id INT NOT NULL,
    INDEX idx_hospi_dossier (dossier_id),
    INDEX idx_hospi_chambre (chambre_id),
    INDEX idx_hospi_statut (statut),
    FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE CASCADE,
    FOREIGN KEY (chambre_id) REFERENCES chambres(id) ON DELETE RESTRICT,
    FOREIGN KEY (medecin_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- Table des demandes de produits
-- =====================================================
CREATE TABLE IF NOT EXISTS demandes_produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    quantite_demandee INT NOT NULL,
    medecin_id INT NOT NULL,
    dossier_id INT NULL,
    date_demande DATETIME DEFAULT CURRENT_TIMESTAMP,
    urgence BOOLEAN DEFAULT FALSE,
    motif TEXT,
    statut ENUM('EN_ATTENTE', 'VALIDEE', 'REFUSEE', 'LIVREE') DEFAULT 'EN_ATTENTE',
    gestionnaire_id INT NULL,
    date_traitement DATETIME NULL,
    commentaire_traitement TEXT,
    INDEX idx_demandes_produit (produit_id),
    INDEX idx_demandes_statut (statut),
    INDEX idx_demandes_urgence (urgence),
    FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT,
    FOREIGN KEY (medecin_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (dossier_id) REFERENCES dossiers_prise_en_charge(id) ON DELETE SET NULL,
    FOREIGN KEY (gestionnaire_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- Table du journal des actions (RGPD)
-- =====================================================
CREATE TABLE IF NOT EXISTS journal_actions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    type_action ENUM('CONNEXION', 'DECONNEXION', 'CREATION', 'MODIFICATION', 'SUPPRESSION', 'CONSULTATION', 'EXPORT') NOT NULL,
    description TEXT,
    date_action DATETIME DEFAULT CURRENT_TIMESTAMP,
    adresse_ip VARCHAR(45),
    entite VARCHAR(100),
    entite_id INT NULL,
    INDEX idx_journal_user (user_id),
    INDEX idx_journal_date (date_action),
    INDEX idx_journal_type (type_action),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- Insertion de l'utilisateur admin par defaut
-- Mot de passe: admin123 (hash avec salt)
-- =====================================================
INSERT INTO users (email, password_hash, nom, prenom, role, actif, date_creation)
VALUES (
    'admin@hsp.fr',
    'dG9rZW5zYWx0MTIzNDU2Nzg=:nKaR9k8lDh0wVs2qF6xP3mB7cY5uE1gJ',
    'Administrateur',
    'Systeme',
    'ADMIN',
    TRUE,
    NOW()
);

-- =====================================================
-- Insertion de quelques chambres de test
-- =====================================================
INSERT INTO chambres (numero, etage, type_chambre, capacite, occupee) VALUES
('101', 1, 'SIMPLE', 1, FALSE),
('102', 1, 'SIMPLE', 1, FALSE),
('103', 1, 'DOUBLE', 2, FALSE),
('201', 2, 'SIMPLE', 1, FALSE),
('202', 2, 'DOUBLE', 2, FALSE),
('301', 3, 'SOINS_INTENSIFS', 1, FALSE),
('URG-01', 0, 'URGENCE', 1, FALSE),
('URG-02', 0, 'URGENCE', 1, FALSE);

-- =====================================================
-- Insertion de quelques produits de test
-- =====================================================
INSERT INTO produits (code, nom, description, categorie, unite_mesure, niveau_dangerosite, seuil_alerte_stock) VALUES
('MED-001', 'Paracetamol 500mg', 'Antalgique et antipyretique', 'Medicaments', 'boite', 'FAIBLE', 50),
('MED-002', 'Ibuprofene 400mg', 'Anti-inflammatoire non steroidien', 'Medicaments', 'boite', 'MOYEN', 30),
('MED-003', 'Morphine 10mg', 'Analgesique opiace', 'Medicaments', 'ampoule', 'TRES_ELEVE', 20),
('MAT-001', 'Compresses steriles', 'Compresses 10x10cm', 'Materiel', 'paquet', 'FAIBLE', 100),
('MAT-002', 'Gants latex M', 'Gants d\'examen taille M', 'Materiel', 'boite', 'FAIBLE', 50),
('MAT-003', 'Seringues 5ml', 'Seringues steriles 5ml', 'Materiel', 'unite', 'FAIBLE', 200);

-- =====================================================
-- Affichage de confirmation
-- =====================================================
SELECT 'Base de donnees hsp_java creee avec succes!' AS message;
SELECT CONCAT('Utilisateur admin cree: admin@hsp.fr') AS info;
SELECT CONCAT('Nombre de chambres: ', COUNT(*)) AS chambres FROM chambres;
SELECT CONCAT('Nombre de produits: ', COUNT(*)) AS produits FROM produits;
