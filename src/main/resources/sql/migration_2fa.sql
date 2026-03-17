-- Migration 2FA : ajout colonnes TOTP sur la table users
-- A executer une seule fois sur la base de donnees hsp_java existante

USE hsp_java;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS totp_secret  VARCHAR(64)  NULL    COMMENT 'Cle secrete TOTP (Base32)',
    ADD COLUMN IF NOT EXISTS totp_enabled BOOLEAN NOT NULL DEFAULT FALSE COMMENT '2FA active';
