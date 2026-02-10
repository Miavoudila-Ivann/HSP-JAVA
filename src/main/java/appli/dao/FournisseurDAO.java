package appli.dao;

import appli.model.Fournisseur;
import appli.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    public Fournisseur findById(int id) {
        String sql = "SELECT * FROM fournisseurs WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du fournisseur : " + e.getMessage());
        }
        return null;
    }

    public Fournisseur findByCode(String code) {
        String sql = "SELECT * FROM fournisseurs WHERE code = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du fournisseur par code : " + e.getMessage());
        }
        return null;
    }

    public List<Fournisseur> findAll() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs WHERE actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    public List<Fournisseur> search(String searchTerm) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs WHERE (nom LIKE ? OR code LIKE ? OR ville LIKE ?) AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    public int insert(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseurs (code, nom, raison_sociale, siret, adresse, code_postal, ville, pays, " +
                "telephone, fax, email, site_web, contact_nom, contact_telephone, contact_email, conditions_paiement, " +
                "delai_livraison_jours, note_evaluation, actif, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fournisseur.getCode());
            stmt.setString(2, fournisseur.getNom());
            stmt.setString(3, fournisseur.getRaisonSociale());
            stmt.setString(4, fournisseur.getSiret());
            stmt.setString(5, fournisseur.getAdresse());
            stmt.setString(6, fournisseur.getCodePostal());
            stmt.setString(7, fournisseur.getVille());
            stmt.setString(8, fournisseur.getPays());
            stmt.setString(9, fournisseur.getTelephone());
            stmt.setString(10, fournisseur.getFax());
            stmt.setString(11, fournisseur.getEmail());
            stmt.setString(12, fournisseur.getSiteWeb());
            stmt.setString(13, fournisseur.getContactNom());
            stmt.setString(14, fournisseur.getContactTelephone());
            stmt.setString(15, fournisseur.getContactEmail());
            stmt.setString(16, fournisseur.getConditionsPaiement());
            if (fournisseur.getDelaiLivraisonJours() != null) {
                stmt.setInt(17, fournisseur.getDelaiLivraisonJours());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
            if (fournisseur.getNoteEvaluation() != null) {
                stmt.setBigDecimal(18, fournisseur.getNoteEvaluation());
            } else {
                stmt.setNull(18, Types.DECIMAL);
            }
            stmt.setBoolean(19, fournisseur.isActif());
            stmt.setTimestamp(20, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du fournisseur : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseurs SET code = ?, nom = ?, raison_sociale = ?, siret = ?, adresse = ?, code_postal = ?, " +
                "ville = ?, pays = ?, telephone = ?, fax = ?, email = ?, site_web = ?, contact_nom = ?, contact_telephone = ?, " +
                "contact_email = ?, conditions_paiement = ?, delai_livraison_jours = ?, note_evaluation = ?, actif = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getCode());
            stmt.setString(2, fournisseur.getNom());
            stmt.setString(3, fournisseur.getRaisonSociale());
            stmt.setString(4, fournisseur.getSiret());
            stmt.setString(5, fournisseur.getAdresse());
            stmt.setString(6, fournisseur.getCodePostal());
            stmt.setString(7, fournisseur.getVille());
            stmt.setString(8, fournisseur.getPays());
            stmt.setString(9, fournisseur.getTelephone());
            stmt.setString(10, fournisseur.getFax());
            stmt.setString(11, fournisseur.getEmail());
            stmt.setString(12, fournisseur.getSiteWeb());
            stmt.setString(13, fournisseur.getContactNom());
            stmt.setString(14, fournisseur.getContactTelephone());
            stmt.setString(15, fournisseur.getContactEmail());
            stmt.setString(16, fournisseur.getConditionsPaiement());
            if (fournisseur.getDelaiLivraisonJours() != null) {
                stmt.setInt(17, fournisseur.getDelaiLivraisonJours());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
            if (fournisseur.getNoteEvaluation() != null) {
                stmt.setBigDecimal(18, fournisseur.getNoteEvaluation());
            } else {
                stmt.setNull(18, Types.DECIMAL);
            }
            stmt.setBoolean(19, fournisseur.isActif());
            stmt.setInt(20, fournisseur.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du fournisseur : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE fournisseurs SET actif = false WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
        }
        return false;
    }

    private Fournisseur mapResultSetToFournisseur(ResultSet rs) throws SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(rs.getInt("id"));
        fournisseur.setCode(rs.getString("code"));
        fournisseur.setNom(rs.getString("nom"));
        fournisseur.setRaisonSociale(rs.getString("raison_sociale"));
        fournisseur.setSiret(rs.getString("siret"));
        fournisseur.setAdresse(rs.getString("adresse"));
        fournisseur.setCodePostal(rs.getString("code_postal"));
        fournisseur.setVille(rs.getString("ville"));
        fournisseur.setPays(rs.getString("pays"));
        fournisseur.setTelephone(rs.getString("telephone"));
        fournisseur.setFax(rs.getString("fax"));
        fournisseur.setEmail(rs.getString("email"));
        fournisseur.setSiteWeb(rs.getString("site_web"));
        fournisseur.setContactNom(rs.getString("contact_nom"));
        fournisseur.setContactTelephone(rs.getString("contact_telephone"));
        fournisseur.setContactEmail(rs.getString("contact_email"));
        fournisseur.setConditionsPaiement(rs.getString("conditions_paiement"));

        int delaiLivraisonJours = rs.getInt("delai_livraison_jours");
        if (!rs.wasNull()) {
            fournisseur.setDelaiLivraisonJours(delaiLivraisonJours);
        }

        fournisseur.setNoteEvaluation(rs.getBigDecimal("note_evaluation"));
        fournisseur.setActif(rs.getBoolean("actif"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            fournisseur.setDateCreation(dateCreation.toLocalDateTime());
        }

        return fournisseur;
    }
}
