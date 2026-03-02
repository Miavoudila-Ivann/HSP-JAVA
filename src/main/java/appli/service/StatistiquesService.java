package appli.service;

import appli.util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatistiquesService {

    public record Indicateurs(
            int nbPatientsHospitalises,
            int nbDossiersEnAttente,
            int nbStocksCritiques,
            double tauxOccupationGlobal
    ) {}

    public Map<String, Integer> getHospitalisationsParSemaine() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
                SELECT DATE_FORMAT(date_entree, '%d/%m') AS semaine,
                       COUNT(*) AS total
                FROM hospitalisations
                WHERE date_entree >= DATE_SUB(CURDATE(), INTERVAL 8 WEEK)
                GROUP BY YEAR(date_entree), WEEK(date_entree, 1), semaine
                ORDER BY MIN(date_entree)
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put("S." + rs.getString("semaine"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Double> getTauxOccupationParType() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = """
                SELECT type_chambre,
                       ROUND(SUM(nb_lits_occupes) * 100.0 / SUM(capacite), 1) AS taux
                FROM v_occupation_chambres
                GROUP BY type_chambre
                ORDER BY taux DESC
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("type_chambre"), rs.getDouble("taux"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Integer> getRepartitionGravite() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
                SELECT niveau_gravite, COUNT(*) AS total
                FROM dossiers_prise_en_charge
                GROUP BY niveau_gravite
                ORDER BY FIELD(niveau_gravite, 'NIVEAU_1','NIVEAU_2','NIVEAU_3','NIVEAU_4','NIVEAU_5')
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = switch (rs.getString("niveau_gravite")) {
                    case "NIVEAU_1" -> "Mineur";
                    case "NIVEAU_2" -> "Modere";
                    case "NIVEAU_3" -> "Serieux";
                    case "NIVEAU_4" -> "Grave";
                    case "NIVEAU_5" -> "Critique";
                    default -> rs.getString("niveau_gravite");
                };
                result.put(label, rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Integer> getProduitsLesPlusDemandes(int limit) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
                SELECT p.nom, COUNT(*) AS total
                FROM demandes_produits dp
                JOIN produits p ON p.id = dp.produit_id
                GROUP BY dp.produit_id, p.nom
                ORDER BY total DESC
                LIMIT ?
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("nom"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Indicateurs getIndicateurs() {
        int nbHospitalises = 0;
        int nbEnAttente = 0;
        int nbStocksCritiques = 0;
        double tauxGlobal = 0;

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // Patients hospitalises en cours
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM hospitalisations WHERE statut = 'EN_COURS'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nbHospitalises = rs.getInt(1);
            }

            // Dossiers en attente
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM dossiers_prise_en_charge WHERE statut = 'EN_ATTENTE'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nbEnAttente = rs.getInt(1);
            }

            // Stocks critiques (quantite totale par produit < seuil_alerte_stock)
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT COUNT(*) FROM (
                        SELECT p.id
                        FROM produits p
                        LEFT JOIN stocks s ON s.produit_id = p.id
                        WHERE p.actif = TRUE
                        GROUP BY p.id, p.seuil_alerte_stock
                        HAVING COALESCE(SUM(s.quantite), 0) < p.seuil_alerte_stock
                    ) AS critique
                    """);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nbStocksCritiques = rs.getInt(1);
            }

            // Taux occupation global
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ROUND(SUM(nb_lits_occupes) * 100.0 / SUM(capacite), 1) FROM v_occupation_chambres");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) tauxGlobal = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Indicateurs(nbHospitalises, nbEnAttente, nbStocksCritiques, tauxGlobal);
    }
}
