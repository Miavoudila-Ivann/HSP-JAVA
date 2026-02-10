-- ============================================================
-- HSP-JAVA - Donnees de test (Seed)
-- A executer apres schema.sql
-- ============================================================

USE hsp_java;

-- ============================================================
-- 1. UTILISATEURS DE TEST
-- Mot de passe par defaut: "password123" pour tous
-- Hash genere avec BCrypt (cost factor 12)
-- ============================================================

INSERT INTO users (email, password_hash, nom, prenom, role, specialite, telephone, actif) VALUES
-- Administrateur
('admin@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Dupont', 'Jean', 'ADMIN', NULL, '0601020304', TRUE),

-- Secretaires
('secretaire1@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Martin', 'Marie', 'SECRETAIRE', NULL, '0601020305', TRUE),
('secretaire2@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Bernard', 'Sophie', 'SECRETAIRE', NULL, '0601020306', TRUE),

-- Medecins
('medecin1@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Leroy', 'Pierre', 'MEDECIN', 'Medecine generale', '0601020307', TRUE),
('medecin2@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Moreau', 'Claire', 'MEDECIN', 'Urgences', '0601020308', TRUE),
('medecin3@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Petit', 'Francois', 'MEDECIN', 'Cardiologie', '0601020309', TRUE),
('medecin4@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Roux', 'Isabelle', 'MEDECIN', 'Pediatrie', '0601020310', TRUE),

-- Gestionnaires de stock
('gestionnaire1@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Fournier', 'Michel', 'GESTIONNAIRE', NULL, '0601020311', TRUE),
('gestionnaire2@hsp.fr', '$2a$12$.PQOxGpNjutdE8Vc8ejIpObzQ15q93w2YEhg6w7O7yQ2UKYBUsiQe', 'Girard', 'Anne', 'GESTIONNAIRE', NULL, '0601020312', TRUE);

-- ============================================================
-- 2. CHAMBRES
-- ============================================================

INSERT INTO chambres (numero, etage, batiment, type_chambre, capacite, nb_lits_occupes, equipements, tarif_journalier, actif) VALUES
-- Rez-de-chaussee - Urgences
('URG-01', 0, 'Urgences', 'URGENCE', 1, 0, 'Monitoring cardiaque, Oxygene, Aspirateur', 150.00, TRUE),
('URG-02', 0, 'Urgences', 'URGENCE', 1, 0, 'Monitoring cardiaque, Oxygene, Aspirateur', 150.00, TRUE),
('URG-03', 0, 'Urgences', 'URGENCE', 2, 0, 'Monitoring cardiaque, Oxygene', 120.00, TRUE),
('URG-04', 0, 'Urgences', 'URGENCE', 2, 0, 'Monitoring cardiaque, Oxygene', 120.00, TRUE),

-- Etage 1 - Chambres simples et doubles
('101', 1, 'Principal', 'SIMPLE', 1, 0, 'TV, Telephone, Wifi', 80.00, TRUE),
('102', 1, 'Principal', 'SIMPLE', 1, 0, 'TV, Telephone, Wifi', 80.00, TRUE),
('103', 1, 'Principal', 'SIMPLE', 1, 0, 'TV, Telephone, Wifi', 80.00, TRUE),
('104', 1, 'Principal', 'DOUBLE', 2, 0, 'TV, Telephone, Wifi', 60.00, TRUE),
('105', 1, 'Principal', 'DOUBLE', 2, 0, 'TV, Telephone, Wifi', 60.00, TRUE),
('106', 1, 'Principal', 'DOUBLE', 2, 0, 'TV, Telephone, Wifi', 60.00, TRUE),

-- Etage 2 - Chambres simples et doubles
('201', 2, 'Principal', 'SIMPLE', 1, 0, 'TV, Telephone, Wifi', 80.00, TRUE),
('202', 2, 'Principal', 'SIMPLE', 1, 0, 'TV, Telephone, Wifi', 80.00, TRUE),
('203', 2, 'Principal', 'DOUBLE', 2, 0, 'TV, Telephone, Wifi', 60.00, TRUE),
('204', 2, 'Principal', 'DOUBLE', 2, 0, 'TV, Telephone, Wifi', 60.00, TRUE),

-- Etage 3 - Soins intensifs et Reanimation
('301', 3, 'Principal', 'SOINS_INTENSIFS', 1, 0, 'Monitoring complet, Respirateur, Pousse-seringues', 350.00, TRUE),
('302', 3, 'Principal', 'SOINS_INTENSIFS', 1, 0, 'Monitoring complet, Respirateur, Pousse-seringues', 350.00, TRUE),
('303', 3, 'Principal', 'REANIMATION', 1, 0, 'Monitoring complet, Respirateur, ECMO, Dialyse', 500.00, TRUE),
('304', 3, 'Principal', 'REANIMATION', 1, 0, 'Monitoring complet, Respirateur, ECMO, Dialyse', 500.00, TRUE),

-- Etage 4 - Pediatrie et Maternite
('401', 4, 'Principal', 'PEDIATRIE', 2, 0, 'Equipement pediatrique, Jeux', 70.00, TRUE),
('402', 4, 'Principal', 'PEDIATRIE', 2, 0, 'Equipement pediatrique, Jeux', 70.00, TRUE),
('403', 4, 'Principal', 'MATERNITE', 1, 0, 'Lit bebe, Equipement maternite', 90.00, TRUE),
('404', 4, 'Principal', 'MATERNITE', 1, 0, 'Lit bebe, Equipement maternite', 90.00, TRUE);

-- ============================================================
-- 3. FOURNISSEURS
-- ============================================================

INSERT INTO fournisseurs (code, nom, raison_sociale, siret, adresse, code_postal, ville, telephone, email, contact_nom, contact_telephone, conditions_paiement, delai_livraison_jours, actif) VALUES
('FOUR-001', 'PharmaSante', 'PharmaSante Distribution SAS', '12345678901234', '15 rue de la Sante', '75015', 'Paris', '0145678901', 'contact@pharmasante.fr', 'Dubois Marc', '0612345678', '30 jours', 3, TRUE),
('FOUR-002', 'MediStock', 'MediStock France SARL', '23456789012345', '8 avenue du Commerce', '69002', 'Lyon', '0456789012', 'commandes@medistock.fr', 'Lemaire Julie', '0623456789', '45 jours', 5, TRUE),
('FOUR-003', 'EquipMedical', 'EquipMedical Pro SA', '34567890123456', '22 boulevard Industrial', '13008', 'Marseille', '0491234567', 'ventes@equipmedical.fr', 'Rousseau Paul', '0634567890', '30 jours fin de mois', 7, TRUE),
('FOUR-004', 'BioLab', 'BioLab Diagnostics', '45678901234567', '5 rue Pasteur', '31000', 'Toulouse', '0561234567', 'info@biolab.fr', 'Garcia Elena', '0645678901', '30 jours', 4, TRUE),
('FOUR-005', 'ConsomHop', 'Consommables Hospitaliers SARL', '56789012345678', '18 zone industrielle', '44000', 'Nantes', '0240123456', 'commandes@consomhop.fr', 'Bertrand Luc', '0656789012', '60 jours', 2, TRUE);

-- ============================================================
-- 4. CATEGORIES DE PRODUITS
-- ============================================================

INSERT INTO categories_produits (code, nom, description, categorie_parent_id, actif) VALUES
('CAT-MED', 'Medicaments', 'Tous les medicaments', NULL, TRUE),
('CAT-MED-ANTAL', 'Antalgiques', 'Medicaments contre la douleur', 1, TRUE),
('CAT-MED-ANTIB', 'Antibiotiques', 'Medicaments antibacteriens', 1, TRUE),
('CAT-MED-CARDIO', 'Cardiologie', 'Medicaments cardiovasculaires', 1, TRUE),
('CAT-MED-STUP', 'Stupefiants', 'Medicaments stupefiants (controle strict)', 1, TRUE),
('CAT-MAT', 'Materiel medical', 'Equipements et dispositifs medicaux', NULL, TRUE),
('CAT-MAT-INSTR', 'Instruments', 'Instruments medicaux', 6, TRUE),
('CAT-MAT-DIAG', 'Diagnostic', 'Materiel de diagnostic', 6, TRUE),
('CAT-CONS', 'Consommables', 'Produits a usage unique', NULL, TRUE),
('CAT-CONS-PANS', 'Pansements', 'Compresses, bandages, sparadraps', 9, TRUE),
('CAT-CONS-INJ', 'Injection', 'Seringues, aiguilles, catheters', 9, TRUE),
('CAT-CONS-PROT', 'Protection', 'Gants, masques, blouses', 9, TRUE),
('CAT-LAB', 'Laboratoire', 'Produits pour analyses', NULL, TRUE);

-- ============================================================
-- 5. PRODUITS
-- ============================================================

INSERT INTO produits (code, code_cip, nom, nom_commercial, description, categorie_id, forme, dosage, unite_mesure, prix_unitaire, niveau_dangerosite, conditions_stockage, necessite_ordonnance, stupefiant, seuil_alerte_stock, fournisseur_principal_id, actif) VALUES
-- Antalgiques
('MED-001', '3400930000001', 'Paracetamol 500mg', 'Doliprane', 'Antalgique et antipyretique', 2, 'COMPRIME', '500mg', 'boite 16', 2.50, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 100, 1, TRUE),
('MED-002', '3400930000002', 'Paracetamol 1g', 'Efferalgan', 'Antalgique et antipyretique', 2, 'COMPRIME', '1g', 'boite 8', 3.20, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 80, 1, TRUE),
('MED-003', '3400930000003', 'Ibuprofene 400mg', 'Advil', 'Anti-inflammatoire non steroidien', 2, 'COMPRIME', '400mg', 'boite 20', 4.50, 'MOYEN', 'Temperature ambiante', FALSE, FALSE, 60, 1, TRUE),
('MED-004', '3400930000004', 'Tramadol 50mg', 'Contramal', 'Antalgique opiace faible', 2, 'GELULE', '50mg', 'boite 30', 8.90, 'ELEVE', 'Temperature ambiante', TRUE, FALSE, 40, 1, TRUE),

-- Stupefiants
('MED-005', '3400930000005', 'Morphine 10mg', 'Skenan', 'Analgesique opiace majeur', 5, 'INJECTABLE', '10mg/ml', 'ampoule', 12.50, 'TRES_ELEVE', 'Coffre securise', TRUE, TRUE, 30, 1, TRUE),
('MED-006', '3400930000006', 'Fentanyl 100mcg', 'Durogesic', 'Analgesique opiace puissant', 5, 'PATCH', '100mcg/h', 'unite', 45.00, 'TRES_ELEVE', 'Coffre securise', TRUE, TRUE, 20, 1, TRUE),

-- Antibiotiques
('MED-007', '3400930000007', 'Amoxicilline 500mg', 'Clamoxyl', 'Antibiotique penicilline', 3, 'GELULE', '500mg', 'boite 12', 5.80, 'MOYEN', 'Temperature ambiante', TRUE, FALSE, 50, 1, TRUE),
('MED-008', '3400930000008', 'Augmentin 1g', 'Augmentin', 'Antibiotique penicilline + acide clavulanique', 3, 'COMPRIME', '1g', 'boite 12', 9.50, 'MOYEN', 'Temperature ambiante', TRUE, FALSE, 40, 1, TRUE),
('MED-009', '3400930000009', 'Ciprofloxacine 500mg', 'Ciflox', 'Antibiotique fluoroquinolone', 3, 'COMPRIME', '500mg', 'boite 10', 12.30, 'MOYEN', 'Temperature ambiante', TRUE, FALSE, 30, 1, TRUE),

-- Cardiologie
('MED-010', '3400930000010', 'Aspirine 100mg', 'Kardegic', 'Antiaggregant plaquettaire', 4, 'COMPRIME', '100mg', 'boite 30', 3.80, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 80, 1, TRUE),
('MED-011', '3400930000011', 'Adrenaline 1mg', 'Adrenaline Aguettant', 'Sympathomimetique urgence', 4, 'INJECTABLE', '1mg/ml', 'ampoule', 8.50, 'ELEVE', '2-8°C', TRUE, FALSE, 50, 1, TRUE),
('MED-012', '3400930000012', 'Atropine 0.5mg', 'Atropine Lavoisier', 'Anticholinergique', 4, 'INJECTABLE', '0.5mg/ml', 'ampoule', 4.20, 'ELEVE', 'Temperature ambiante', TRUE, FALSE, 40, 1, TRUE),

-- Consommables - Pansements
('CONS-001', NULL, 'Compresses steriles 10x10', NULL, 'Compresses non tissees steriles', 10, 'DISPOSITIF', '10x10cm', 'sachet 10', 1.80, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 200, 5, TRUE),
('CONS-002', NULL, 'Compresses steriles 5x5', NULL, 'Compresses non tissees steriles', 10, 'DISPOSITIF', '5x5cm', 'sachet 10', 1.20, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 200, 5, TRUE),
('CONS-003', NULL, 'Bande Velpeau 5cm', NULL, 'Bande de contention elastique', 10, 'DISPOSITIF', '5cm x 4m', 'unite', 2.50, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 100, 5, TRUE),
('CONS-004', NULL, 'Sparadrap microporeux', NULL, 'Sparadrap hypoallergenique', 10, 'DISPOSITIF', '2.5cm x 5m', 'rouleau', 3.80, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 80, 5, TRUE),

-- Consommables - Injection
('CONS-005', NULL, 'Seringue 5ml', NULL, 'Seringue Luer sterile', 11, 'DISPOSITIF', '5ml', 'unite', 0.15, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 500, 5, TRUE),
('CONS-006', NULL, 'Seringue 10ml', NULL, 'Seringue Luer sterile', 11, 'DISPOSITIF', '10ml', 'unite', 0.18, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 500, 5, TRUE),
('CONS-007', NULL, 'Aiguille 21G', NULL, 'Aiguille hypodermique verte', 11, 'DISPOSITIF', '21G 0.8x40mm', 'unite', 0.05, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 1000, 5, TRUE),
('CONS-008', NULL, 'Catheter IV 18G', NULL, 'Catheter peripherique vert', 11, 'DISPOSITIF', '18G', 'unite', 1.20, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 200, 5, TRUE),
('CONS-009', NULL, 'Perfuseur', NULL, 'Perfuseur avec robinet', 11, 'DISPOSITIF', 'standard', 'unite', 1.80, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 150, 5, TRUE),

-- Consommables - Protection
('CONS-010', NULL, 'Gants latex M', NULL, 'Gants d examen latex taille M', 12, 'DISPOSITIF', 'M', 'boite 100', 8.50, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 50, 5, TRUE),
('CONS-011', NULL, 'Gants latex L', NULL, 'Gants d examen latex taille L', 12, 'DISPOSITIF', 'L', 'boite 100', 8.50, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 50, 5, TRUE),
('CONS-012', NULL, 'Gants nitrile M', NULL, 'Gants nitrile sans latex taille M', 12, 'DISPOSITIF', 'M', 'boite 100', 12.00, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 40, 5, TRUE),
('CONS-013', NULL, 'Masque chirurgical', NULL, 'Masque 3 plis type II', 12, 'DISPOSITIF', 'adulte', 'boite 50', 6.00, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 100, 5, TRUE),
('CONS-014', NULL, 'Masque FFP2', NULL, 'Masque de protection FFP2', 12, 'DISPOSITIF', 'adulte', 'boite 20', 15.00, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 50, 5, TRUE),
('CONS-015', NULL, 'Charlotte', NULL, 'Coiffe non tissee', 12, 'DISPOSITIF', 'unique', 'boite 100', 4.00, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 50, 5, TRUE),
('CONS-016', NULL, 'Surblouse', NULL, 'Blouse visiteur non tissee', 12, 'DISPOSITIF', 'unique', 'unite', 0.80, 'FAIBLE', 'Temperature ambiante', FALSE, FALSE, 100, 5, TRUE);

-- ============================================================
-- 6. ASSOCIATIONS PRODUITS-FOURNISSEURS
-- ============================================================

INSERT INTO produits_fournisseurs (produit_id, fournisseur_id, reference_fournisseur, prix_achat, delai_livraison_jours, quantite_minimum_commande, est_principal) VALUES
-- PharmaSante (medicaments)
(1, 1, 'PS-PARA500', 1.80, 3, 10, TRUE),
(2, 1, 'PS-PARA1G', 2.40, 3, 10, TRUE),
(3, 1, 'PS-IBU400', 3.20, 3, 10, TRUE),
(4, 1, 'PS-TRAM50', 6.50, 3, 5, TRUE),
(5, 1, 'PS-MORPH10', 9.00, 2, 5, TRUE),
(7, 1, 'PS-AMOX500', 4.20, 3, 10, TRUE),
(8, 1, 'PS-AUGM1G', 7.00, 3, 10, TRUE),
(10, 1, 'PS-KARD100', 2.80, 3, 10, TRUE),
(11, 1, 'PS-ADRE1', 6.00, 2, 10, TRUE),

-- MediStock (medicaments alternatif)
(1, 2, 'MS-DOLIP500', 1.90, 5, 20, FALSE),
(3, 2, 'MS-ADVIL400', 3.40, 5, 20, FALSE),

-- ConsomHop (consommables)
(13, 5, 'CH-COMP1010', 1.20, 2, 50, TRUE),
(14, 5, 'CH-COMP55', 0.80, 2, 50, TRUE),
(15, 5, 'CH-VELP5', 1.80, 2, 30, TRUE),
(17, 5, 'CH-SER5', 0.10, 2, 100, TRUE),
(18, 5, 'CH-SER10', 0.12, 2, 100, TRUE),
(19, 5, 'CH-AIG21', 0.03, 2, 200, TRUE),
(22, 5, 'CH-GLTXM', 6.00, 2, 20, TRUE),
(23, 5, 'CH-GLTXL', 6.00, 2, 20, TRUE),
(25, 5, 'CH-MSKCH', 4.00, 2, 50, TRUE),
(26, 5, 'CH-MSKFFP', 11.00, 2, 20, TRUE);

-- ============================================================
-- 7. EMPLACEMENTS DE STOCK
-- ============================================================

INSERT INTO emplacements_stock (code, nom, description, type_emplacement, temperature_controlee, temperature_cible, responsable_id, actif) VALUES
('PHARM-CENT', 'Pharmacie centrale', 'Stock principal de la pharmacie', 'PHARMACIE', FALSE, NULL, 8, TRUE),
('PHARM-FRIGO', 'Pharmacie - Frigo', 'Medicaments thermosensibles', 'FRIGO', TRUE, 5.00, 8, TRUE),
('PHARM-COFFRE', 'Pharmacie - Coffre', 'Stupefiants et produits controles', 'COFFRE', FALSE, NULL, 8, TRUE),
('RESERVE-A', 'Reserve A', 'Reserve principale consommables', 'RESERVE', FALSE, NULL, 9, TRUE),
('RESERVE-B', 'Reserve B', 'Reserve secondaire', 'RESERVE', FALSE, NULL, 9, TRUE),
('URG-STOCK', 'Stock Urgences', 'Stock dedie aux urgences', 'URGENCE', FALSE, NULL, 8, TRUE),
('URG-CHARIOT', 'Chariot Urgences', 'Chariot d urgence mobile', 'URGENCE', FALSE, NULL, 8, TRUE),
('BLOC-1', 'Bloc operatoire 1', 'Stock bloc operatoire 1', 'BLOC', FALSE, NULL, 8, TRUE),
('SI-STOCK', 'Soins intensifs', 'Stock soins intensifs', 'SERVICE', FALSE, NULL, 8, TRUE),
('MATERNITE', 'Maternite', 'Stock maternite', 'SERVICE', FALSE, NULL, 8, TRUE);

-- ============================================================
-- 8. STOCKS INITIAUX
-- ============================================================

INSERT INTO stocks (produit_id, emplacement_id, lot, quantite, quantite_reservee, date_peremption, prix_unitaire_achat, fournisseur_id, numero_commande) VALUES
-- Pharmacie centrale - Medicaments
(1, 1, 'LOT2024A001', 200, 0, '2026-06-30', 1.80, 1, 'CMD-2024-001'),
(2, 1, 'LOT2024A002', 150, 0, '2026-06-30', 2.40, 1, 'CMD-2024-001'),
(3, 1, 'LOT2024A003', 100, 0, '2026-03-31', 3.20, 1, 'CMD-2024-001'),
(4, 1, 'LOT2024A004', 80, 0, '2026-05-31', 6.50, 1, 'CMD-2024-002'),
(7, 1, 'LOT2024A007', 120, 0, '2025-12-31', 4.20, 1, 'CMD-2024-002'),
(8, 1, 'LOT2024A008', 80, 0, '2025-11-30', 7.00, 1, 'CMD-2024-002'),
(9, 1, 'LOT2024A009', 60, 0, '2026-01-31', 8.90, 1, 'CMD-2024-002'),
(10, 1, 'LOT2024A010', 150, 0, '2026-08-31', 2.80, 1, 'CMD-2024-003'),
(12, 1, 'LOT2024A012', 60, 0, '2026-04-30', 3.00, 1, 'CMD-2024-003'),

-- Pharmacie frigo
(11, 2, 'LOT2024F001', 80, 0, '2025-09-30', 6.00, 1, 'CMD-2024-003'),

-- Coffre stupefiants
(5, 3, 'LOT2024S001', 50, 0, '2026-02-28', 9.00, 1, 'CMD-2024-004'),
(6, 3, 'LOT2024S002', 30, 0, '2026-04-30', 32.00, 1, 'CMD-2024-004'),

-- Reserve A - Consommables
(13, 4, 'LOT2024C001', 500, 0, '2027-12-31', 1.20, 5, 'CMD-2024-010'),
(14, 4, 'LOT2024C002', 400, 0, '2027-12-31', 0.80, 5, 'CMD-2024-010'),
(15, 4, 'LOT2024C003', 200, 0, '2027-12-31', 1.80, 5, 'CMD-2024-010'),
(16, 4, 'LOT2024C004', 150, 0, '2027-12-31', 2.70, 5, 'CMD-2024-010'),
(17, 4, 'LOT2024C005', 1000, 0, NULL, 0.10, 5, 'CMD-2024-011'),
(18, 4, 'LOT2024C006', 800, 0, NULL, 0.12, 5, 'CMD-2024-011'),
(19, 4, 'LOT2024C007', 2000, 0, NULL, 0.03, 5, 'CMD-2024-011'),
(20, 4, 'LOT2024C008', 300, 0, NULL, 0.85, 5, 'CMD-2024-011'),
(21, 4, 'LOT2024C009', 250, 0, NULL, 1.30, 5, 'CMD-2024-011'),
(22, 4, 'LOT2024C010', 100, 0, '2027-06-30', 6.00, 5, 'CMD-2024-012'),
(23, 4, 'LOT2024C011', 80, 0, '2027-06-30', 6.00, 5, 'CMD-2024-012'),
(24, 4, 'LOT2024C012', 60, 0, '2027-06-30', 8.50, 5, 'CMD-2024-012'),
(25, 4, 'LOT2024C013', 200, 0, '2027-01-31', 4.00, 5, 'CMD-2024-012'),
(26, 4, 'LOT2024C014', 100, 0, '2027-01-31', 11.00, 5, 'CMD-2024-012'),
(27, 4, 'LOT2024C015', 150, 0, NULL, 2.80, 5, 'CMD-2024-012'),
(28, 4, 'LOT2024C016', 200, 0, NULL, 0.55, 5, 'CMD-2024-012'),

-- Stock Urgences
(1, 6, 'LOT2024U001', 50, 0, '2026-06-30', 1.80, 1, 'CMD-2024-020'),
(3, 6, 'LOT2024U002', 30, 0, '2026-03-31', 3.20, 1, 'CMD-2024-020'),
(11, 6, 'LOT2024U003', 20, 0, '2025-09-30', 6.00, 1, 'CMD-2024-020'),
(12, 6, 'LOT2024U004', 20, 0, '2026-04-30', 3.00, 1, 'CMD-2024-020'),
(17, 6, 'LOT2024U005', 100, 0, NULL, 0.10, 5, 'CMD-2024-021'),
(19, 6, 'LOT2024U006', 200, 0, NULL, 0.03, 5, 'CMD-2024-021'),
(20, 6, 'LOT2024U007', 50, 0, NULL, 0.85, 5, 'CMD-2024-021'),

-- Chariot urgences
(5, 7, 'LOT2024E001', 10, 0, '2026-02-28', 9.00, 1, 'CMD-2024-022'),
(11, 7, 'LOT2024E002', 10, 0, '2025-09-30', 6.00, 1, 'CMD-2024-022'),
(12, 7, 'LOT2024E003', 10, 0, '2026-04-30', 3.00, 1, 'CMD-2024-022');

-- ============================================================
-- 9. PATIENTS DE TEST
-- ============================================================

INSERT INTO patients (numero_securite_sociale, nom, prenom, date_naissance, sexe, groupe_sanguin, adresse, code_postal, ville, telephone, telephone_mobile, email, personne_contact_nom, personne_contact_telephone, personne_contact_lien, medecin_traitant, allergies_connues, antecedents_medicaux, cree_par) VALUES
('185056912345678', 'Dubois', 'Jean-Pierre', '1985-05-15', 'M', 'A+', '12 rue de la Liberte', '75011', 'Paris', '0145678901', '0612345678', 'jp.dubois@email.fr', 'Dubois Marie', '0623456789', 'Epouse', 'Dr Martin', 'Penicilline', 'Hypertension arterielle', 2),
('290037845678901', 'Laurent', 'Sophie', '1990-03-22', 'F', 'O+', '8 avenue des Fleurs', '69003', 'Lyon', '0478901234', '0687654321', 'sophie.laurent@email.fr', 'Laurent Pierre', '0698765432', 'Pere', 'Dr Petit', NULL, 'RAS', 2),
('175129034567890', 'Martin', 'Philippe', '1975-12-08', 'M', 'B-', '45 boulevard Voltaire', '13005', 'Marseille', '0491234567', '0654321098', 'p.martin@email.fr', 'Martin Claire', '0665432109', 'Fille', 'Dr Leroy', 'Aspirine, Latex', 'Diabete type 2, Cholesterol', 2),
('200087612345098', 'Petit', 'Emma', '2000-08-30', 'F', 'AB+', '3 place de la Mairie', '31000', 'Toulouse', '0561234567', '0676543210', 'emma.petit@email.fr', 'Petit Marc', '0687654321', 'Frere', 'Dr Moreau', NULL, 'Asthme', 2),
('165044523456789', 'Moreau', 'Jacques', '1965-04-18', 'M', 'O-', '27 rue du Commerce', '44000', 'Nantes', '0240567890', '0698765432', NULL, 'Moreau Jeanne', '0609876543', 'Epouse', 'Dr Fournier', 'Sulfamides', 'Infarctus 2018, Pacemaker', 2),
('195116734567890', 'Garcia', 'Maria', '1995-11-25', 'F', 'A-', '15 rue des Lilas', '33000', 'Bordeaux', '0556789012', '0612345679', 'maria.garcia@email.fr', 'Garcia Carlos', '0623456780', 'Pere', 'Dr Roux', NULL, 'RAS', 2),
('155078945678901', 'Bernard', 'Michel', '1955-07-12', 'M', 'B+', '88 avenue Jean Jaures', '59000', 'Lille', '0320123456', '0645678901', NULL, 'Bernard Anne', '0656789012', 'Fille', 'Dr Girard', 'Iode', 'Cancer prostate traite, BPCO', 2),
('210019056789012', 'Thomas', 'Lea', '2021-01-05', 'F', 'O+', '5 rue Pasteur', '67000', 'Strasbourg', '0388901234', '0667890123', 'famille.thomas@email.fr', 'Thomas Julie', '0678901234', 'Mere', 'Dr Bonnet', NULL, 'Prematuree 34SA', 2);

-- ============================================================
-- 10. QUELQUES DOSSIERS DE TEST
-- ============================================================

INSERT INTO dossiers_prise_en_charge (numero_dossier, patient_id, date_creation, date_admission, motif_admission, niveau_gravite, mode_arrivee, symptomes, constantes_vitales, antecedents, allergies, traitement_en_cours, statut, priorite_triage, medecin_responsable_id, cree_par) VALUES
('DPC-20240115-0001', 1, '2024-01-15 08:30:00', '2024-01-15 08:45:00', 'Douleur thoracique', 'NIVEAU_4', 'AMBULANCE', 'Douleur retrosternale irradiant vers le bras gauche, sueurs', '{"tension": "160/95", "pouls": 95, "temperature": 37.2, "saturation": 96}', 'Hypertension arterielle', 'Penicilline', 'Amlodipine 5mg', 'EN_COURS', 80, 4, 2),
('DPC-20240115-0002', 3, '2024-01-15 09:15:00', '2024-01-15 09:30:00', 'Malaise hypoglycemique', 'NIVEAU_3', 'PERSONNEL', 'Sueurs, tremblements, confusion legere', '{"tension": "110/70", "pouls": 88, "temperature": 36.8, "saturation": 98, "glycemie": 0.55}', 'Diabete type 2', 'Aspirine, Latex', 'Metformine 1000mg x2', 'EN_COURS', 60, 4, 2),
('DPC-20240115-0003', 4, '2024-01-15 10:00:00', NULL, 'Crise d asthme', 'NIVEAU_2', 'PERSONNEL', 'Dyspnee, sibilants bilateraux', '{"tension": "125/80", "pouls": 100, "temperature": 37.0, "saturation": 93}', 'Asthme', NULL, 'Ventoline a la demande', 'EN_ATTENTE', 40, NULL, 2),
('DPC-20240115-0004', 6, '2024-01-15 11:30:00', NULL, 'Entorse cheville', 'NIVEAU_1', 'PERSONNEL', 'Douleur cheville droite apres chute, oedeme', '{"tension": "118/72", "pouls": 75, "temperature": 36.9, "saturation": 99}', 'RAS', NULL, NULL, 'EN_ATTENTE', 10, NULL, 2);

-- ============================================================
-- 11. LOGS INITIAUX
-- ============================================================

INSERT INTO journal_actions (user_id, type_action, description, adresse_ip, entite) VALUES
(1, 'CREATION', 'Initialisation de la base de donnees', '127.0.0.1', 'SYSTEME'),
(1, 'CREATION', 'Creation des utilisateurs de test', '127.0.0.1', 'users'),
(1, 'CREATION', 'Creation des chambres', '127.0.0.1', 'chambres'),
(1, 'CREATION', 'Creation du catalogue produits', '127.0.0.1', 'produits'),
(1, 'CREATION', 'Initialisation des stocks', '127.0.0.1', 'stocks');

-- ============================================================
-- VERIFICATION DES DONNEES
-- ============================================================

SELECT '=== RESUME DES DONNEES INSEREES ===' AS info;
SELECT CONCAT('Utilisateurs: ', COUNT(*)) AS compteur FROM users;
SELECT CONCAT('Chambres: ', COUNT(*)) AS compteur FROM chambres;
SELECT CONCAT('Fournisseurs: ', COUNT(*)) AS compteur FROM fournisseurs;
SELECT CONCAT('Categories: ', COUNT(*)) AS compteur FROM categories_produits;
SELECT CONCAT('Produits: ', COUNT(*)) AS compteur FROM produits;
SELECT CONCAT('Emplacements stock: ', COUNT(*)) AS compteur FROM emplacements_stock;
SELECT CONCAT('Lignes de stock: ', COUNT(*)) AS compteur FROM stocks;
SELECT CONCAT('Patients: ', COUNT(*)) AS compteur FROM patients;
SELECT CONCAT('Dossiers: ', COUNT(*)) AS compteur FROM dossiers_prise_en_charge;

SELECT '=== UTILISATEURS ===' AS info;
SELECT email, CONCAT(prenom, ' ', nom) AS nom_complet, role FROM users ORDER BY role, nom;

SELECT '=== STOCKS ALERTES ===' AS info;
SELECT * FROM v_stocks_details WHERE statut_stock != 'OK';
