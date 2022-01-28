package com.ensta.myfilmlist.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.model.Film;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class JpaFilmDao implements FilmDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Film> findAll() {
        String sql = "SELECT f FROM Film f";

        return entityManager
                .createQuery(sql, Film.class)
                .getResultList();
    }

    public Film save(Film film) {
        entityManager.persist(film);
        return entityManager.find(Film.class, film.getId());
    }

    public Optional<Film> findById(long id) {
        Film film = entityManager.find(Film.class, id);
        return Optional.ofNullable(film);
    }

    public void delete(Film film) {
        if (entityManager.find(Film.class, film.getId()) != null) {
            if (!entityManager.contains(film)) {
                film = entityManager.merge(film);
            }
            entityManager.remove(film);
        }
    }

    public List<Film> findByRealisateurId(long realisateurId) {
        String sql = String.format("SELECT f FROM Film f WHERE f.realisateur = %d", realisateurId);

        return entityManager
                .createQuery(sql, Film.class)
                .getResultList();
    }

    public Film update(Film film) {
        entityManager.merge(film);
        return entityManager.find(Film.class, film.getId());
    }
}
