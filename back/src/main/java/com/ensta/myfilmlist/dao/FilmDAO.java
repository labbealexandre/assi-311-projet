package com.ensta.myfilmlist.dao;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.model.Film;

public interface FilmDAO {
    /**
     * Retourne tous les films en base
     * 
     * @return les films en base
     */
    public List<Film> findAll();

    /**
     * Ajoute un nouveau film en base
     * 
     * @return le nouveau film
     */
    public Film save(Film film);

    /**
     * Retourne un optional d'un film en base connaissant l'id
     * 
     * @return l'optional
     */
    public Optional<Film> findById(long id);

    /**
     * Supprime un film en base
     * 
     * @return
     */
    public void delete(Film film);

    /**
     * Retourne les films en base connaissant l'id du réalisateur
     * 
     * @return les films en base
     */
    public List<Film> findByRealisateurId(long realisateurId);

    /**
     * Met à jour un film en base
     * 
     * @return le film mis à jour
     */
    public Film update(Film film);
}
