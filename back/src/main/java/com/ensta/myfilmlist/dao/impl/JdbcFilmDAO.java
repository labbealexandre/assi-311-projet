package com.ensta.myfilmlist.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcFilmDAO implements FilmDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Film> findAll() {
        String sql = "SELECT f.id as film_id, f.titre, f.duree, r.id as realisateur_id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Film f JOIN Realisateur r ON f.realisateur_id = r.id";

        return jdbcTemplate.query(sql, (resultSet, rownum) -> {
            Film film = new Film();
            film.setId(resultSet.getLong("film_id"));
            film.setTitre(resultSet.getString("titre"));
            film.setDuree(resultSet.getInt("duree"));

            Realisateur realisateur = new Realisateur();
            realisateur.setId(resultSet.getLong("realisateur_id"));
            realisateur.setNom(resultSet.getString("nom"));
            realisateur.setPrenom(resultSet.getString("prenom"));
            realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
            realisateur.setCelebre(resultSet.getBoolean("celebre"));

            film.setRealisateur(realisateur);

            return film;
        });
    }

    public Film save(Film film) {
        String sql = "INSERT INTO Film(titre, duree, realisateur_id) VALUES(?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = conn -> {
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getTitre());
            statement.setInt(2, film.getDuree());
            statement.setLong(3, film.getRealisateur().getId());

            return statement;
        };
        jdbcTemplate.update(creator, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        return film;
    }

    public Optional<Film> findById(long id) {
        String sql = "SELECT f.id as film_id, f.titre, f.duree, r.id as realisateur_id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Film f JOIN Realisateur r ON f.realisateur_id = r.id WHERE f.id = ?";

        try {
            Film found = jdbcTemplate.queryForObject(sql, (resultSet, rownum) -> {
                Film film = new Film();
                film.setId(resultSet.getLong("film_id"));
                film.setTitre(resultSet.getString("titre"));
                film.setDuree(resultSet.getInt("duree"));

                Realisateur realisateur = new Realisateur();
                realisateur.setId(resultSet.getLong("realisateur_id"));
                realisateur.setNom(resultSet.getString("nom"));
                realisateur.setPrenom(resultSet.getString("prenom"));
                realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
                realisateur.setCelebre(resultSet.getBoolean("celebre"));

                film.setRealisateur(realisateur);

                return film;
            }, id);

            return Optional.of(found);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void delete(Film film) {
        String sql = "DELETE FROM Film f WHERE id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    public List<Film> findByRealisateurId(long realisateurId) {
        String sql = "SELECT f.id, f.titre, f.duree, r.id as realisateur_id, r.nom, r.prenom, r.date_naissance, r.celebre FROM Film f JOIN Realisateur r ON f.realisateur_id = r.id WHERE f.realisateur_id = ?";

        return jdbcTemplate.query(sql, (resultSet, rownum) -> {
            Film film = new Film();
            film.setId(resultSet.getLong("id"));
            film.setTitre(resultSet.getString("titre"));
            film.setDuree(resultSet.getInt("duree"));

            Realisateur realisateur = new Realisateur();
            realisateur.setId(resultSet.getLong("realisateur_id"));
            realisateur.setNom(resultSet.getString("nom"));
            realisateur.setPrenom(resultSet.getString("prenom"));
            realisateur.setDateNaissance(resultSet.getDate("date_naissance").toLocalDate());
            realisateur.setCelebre(resultSet.getBoolean("celebre"));

            film.setRealisateur(realisateur);

            return film;
        }, realisateurId);
    }

    public Film update(Film film) {
        String sql = "UPDATE film SET titre = ?, duree = ?, realisateur_id = ? WHERE id = ?";

        jdbcTemplate.update(sql, film.getTitre(), film.getDuree(), film.getRealisateur().getId(), film.getId());

        return this.findById(film.getId()).get();
    }
}
