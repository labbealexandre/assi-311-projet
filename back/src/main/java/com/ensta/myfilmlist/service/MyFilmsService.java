package com.ensta.myfilmlist.service;

import java.util.List;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;

public interface MyFilmsService {

    /**
     * Retourne le realisateur avec l'attribut celebre mis a jour en fonciton du
     * nombre de ses films
     * 
     * @param realisateur le realisateur a mettre a jour
     * @return le realisateur mis a jour
     * @throws ServiceException
     */
    public Realisateur updateRealisateurCelebre(Realisateur realisateur) throws ServiceException;

    /**
     * Calcule la durée totale des films
     * 
     * @param films
     * @return
     */
    public int calculerDureeTotale(List<Film> films);

    /**
     * Calcule la note moyenne des notes
     * 
     * @param notes
     * @return
     */
    public double calculerNoteMoyenne(double[] notes);

    /**
     * Retourne l'ensemble des films
     * 
     * @return la liste des films
     * @throws ServiceException
     */
    public List<FilmDTO> findAllFilms() throws ServiceException;

    /**
     * Créer un nouveau film
     * 
     * @param filmForm les données du film
     * @return le film crée
     * @throws ServiceException
     */
    public FilmDTO createFilm(FilmForm filmForm) throws ServiceException;

    /**
     * Retourne tous les résalisateurs
     * 
     * @return les réalisateurs
     * @throws ServiceException
     */
    public List<RealisateurDTO> findAllRealisateurs() throws ServiceException;

    /**
     * Retourne un réalisateur connaissant son nom et son prénom
     * 
     * @param nom    le nom du réalisateur
     * @param prenom le prenom du réalisateur
     * @return le réalisateur
     * @throws ServiceException
     */
    public RealisateurDTO findRealisateurByNomAndPrenom(String nom, String prenom) throws ServiceException;

    /**
     * Retourne un film connaissant son id
     * 
     * @param id l'id du film
     * @return le film
     * @throws ServiceException
     */
    public FilmDTO findFilmById(long id) throws ServiceException;

    /**
     * Retourne un film connaissant l'id de son réalisateur
     * 
     * @param id l'id du réalisateur
     * @return le film
     * @throws ServiceException
     */
    public List<FilmDTO> findFilmByRealisateurId(long id) throws ServiceException;

    /**
     * Supprime un film
     * 
     * @param id l'id du film
     * @return
     * @throws ServiceException
     */
    public void deleteFilm(long id) throws ServiceException;

    /**
     * Met à jour un film
     * 
     * @param l'id     du film
     * @param filmForm les nouvelles données du film
     * @return le film mis à jour
     * @throws ServiceException
     */
    public FilmDTO updateFilm(long id, FilmForm filmForm) throws ServiceException;
}
