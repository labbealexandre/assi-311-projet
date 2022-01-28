package com.ensta.myfilmlist.dao;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.model.Realisateur;

public interface RealisateurDAO {
    /**
     * Retourne tous les réalisateurs en base
     * 
     * @return les réalisateurs en base
     */
    public List<Realisateur> findAll();

    /**
     * Retourne un réalistauer en base connaissant son nom et son prénom
     * 
     * @return les films en base
     */
    public Realisateur findByNomAndPrenom(String nom, String prenom);

    /**
     * Retourne un optional d'un réalisateur en base connaissant l'id
     * 
     * @return l'optional'
     */
    public Optional<Realisateur> findById(long id);

    /**
     * Met à jour un réalisateur en base
     * 
     * @return le réalisateur mis à jour
     */
    public Realisateur update(Realisateur realisateur);
}
