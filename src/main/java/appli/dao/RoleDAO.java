package appli.dao;

import appli.model.User;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleDAO {

    public List<User.Role> findAll() {
        return Arrays.asList(User.Role.values());
    }

    public List<User.Role> findUsedRoles() {
        List<User.Role> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT role FROM users ORDER BY role";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(User.Role.valueOf(rs.getString("role")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des roles utilises : " + e.getMessage());
        }
        return roles;
    }

    public int countByRole(User.Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des utilisateurs par role : " + e.getMessage());
        }
        return 0;
    }

    public List<User> findUsersByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY nom, prenom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des utilisateurs par role : " + e.getMessage());
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setSpecialite(rs.getString("specialite"));
        user.setTelephone(rs.getString("telephone"));
        user.setActif(rs.getBoolean("actif"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            user.setDateCreation(dateCreation.toLocalDateTime());
        }
        Timestamp derniereConnexion = rs.getTimestamp("derniere_connexion");
        if (derniereConnexion != null) {
            user.setDerniereConnexion(derniereConnexion.toLocalDateTime());
        }
        user.setTentativesConnexion(rs.getInt("tentatives_connexion"));
        user.setCompteVerrouille(rs.getBoolean("compte_verrouille"));
        Timestamp dateVerrouillage = rs.getTimestamp("date_verrouillage");
        if (dateVerrouillage != null) {
            user.setDateVerrouillage(dateVerrouillage.toLocalDateTime());
        }
        return user;
    }
}
