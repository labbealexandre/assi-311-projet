package com.ensta.myfilmlist.dao.impl;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.model.Realisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRealisateurDAO implements RealisateurDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Realisateur> findAll() {
        String sql = "SELECT r.id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Realisateur r";

        return jdbcTemplate.query(sql, (resultSet, rownum) -> {
            Realisateur realisateur = new Realisateur();
            realisateur.setId(resultSet.getLong("id"));
            realisateur.setNom(resultSet.getString("nom"));
            realisateur.setPrenom(resultSet.getString("prenom"));
            realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
            realisateur.setCelebre(resultSet.getBoolean("celebre"));

            return realisateur;
        });
    }

    public Realisateur findByNomAndPrenom(String nom, String prenom) {
        String sql = "SELECT r.id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Realisateur r WHERE nom = ? and prenom = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rownum) -> {
                Realisateur realisateur = new Realisateur();
                realisateur.setId(resultSet.getLong("id"));
                realisateur.setNom(resultSet.getString("nom"));
                realisateur.setPrenom(resultSet.getString("prenom"));
                realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
                realisateur.setCelebre(resultSet.getBoolean("celebre"));

                return realisateur;
            }, nom, prenom);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<Realisateur> findById(long id) {
        String sql = "SELECT r.id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Realisateur r WHERE id = ?";

        try {
            Realisateur found = jdbcTemplate.queryForObject(sql, (resultSet, rownum) -> {
                Realisateur realisateur = new Realisateur();
                realisateur.setId(resultSet.getLong("id"));
                realisateur.setNom(resultSet.getString("nom"));
                realisateur.setPrenom(resultSet.getString("prenom"));
                realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
                realisateur.setCelebre(resultSet.getBoolean("celebre"));

                return realisateur;
            }, id);

            return Optional.of(found);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Realisateur update(Realisateur realisateur) {
        String sql = "UPDATE realisateur SET nom = ?, prenom = ?, date_naissance = ?, celebre = ? WHERE id = ?";
        jdbcTemplate.update(sql, realisateur.getNom(), realisateur.getPrenom(),
                realisateur.getDateNaissance(),
                realisateur.getCelebre(), realisateur.getId());

        return this.findById(realisateur.getId()).get();
    }
}
