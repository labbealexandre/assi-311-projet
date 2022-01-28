package com.ensta.myfilmlist.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.mapper.FilmMapper;
import com.ensta.myfilmlist.mapper.RealisateurMapper;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;
import com.ensta.myfilmlist.service.MyFilmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyFilmsServiceImpl implements MyFilmsService {

    private static final int NB_FILMS_MIN_REALISATEUR_CELEBRE = 3;

    @Autowired
    private FilmDAO filmDAO;

    @Autowired
    private RealisateurDAO realisateurDAO;

    @Override
    @Transactional
    public Realisateur updateRealisateurCelebre(Realisateur realisateur) throws ServiceException {

        if (realisateur == null) {
            throw new ServiceException("Realisateur ne peut pas etre null.");
        }

        if (realisateur.getFilmRealises() == null) {
            throw new ServiceException("filmRealises de Realisateur ne peut pas etre null.");
        }

        realisateur.setCelebre(realisateur.getFilmRealises().size() >= NB_FILMS_MIN_REALISATEUR_CELEBRE);

        return realisateur;
    }

    @Override
    public int calculerDureeTotale(List<Film> films) {
        return films.stream().map(Film::getDuree).reduce(0, Integer::sum);
    }

    @Override
    public double calculerNoteMoyenne(double[] notes) {
        if (notes.length == 0) {
            return 0;
        }

        double moyenne = Arrays.stream(notes).average().getAsDouble();
        moyenne = Math.round(moyenne * Math.pow(10, 2)) / Math.pow(10, 2);
        return moyenne;
    }

    @Override
    public List<FilmDTO> findAllFilms() throws ServiceException {
        try {
            List<Film> films = filmDAO.findAll();
            return FilmMapper.convertFilmToFilmDTOs(films);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public FilmDTO createFilm(FilmForm filmForm) throws ServiceException {
        try {
            Film film = FilmMapper.convertFilmFormToFilm(filmForm);

            Optional<Realisateur> rOptional = realisateurDAO.findById(film.getRealisateur().getId());

            if (rOptional.isEmpty()) {
                throw new ServiceException("Realisateur not found");
            }

            Realisateur realisateur = rOptional.get();
            film.setRealisateur(realisateur);
            film = filmDAO.save(film);

            List<Film> filmRealises = filmDAO.findByRealisateurId(realisateur.getId());
            realisateur.setFilmRealises(filmRealises);
            this.updateRealisateurCelebre(realisateur);
            realisateurDAO.update(realisateur);

            return FilmMapper.convertFilmToFilmDTO(film);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<RealisateurDTO> findAllRealisateurs() throws ServiceException {
        try {
            List<Realisateur> realisateurs = realisateurDAO.findAll();
            return RealisateurMapper.convertRealisateurToRealisateurDTOs(realisateurs);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public RealisateurDTO findRealisateurByNomAndPrenom(String nom, String prenom) throws ServiceException {
        Realisateur realisateur = realisateurDAO.findByNomAndPrenom(nom, prenom);

        if (realisateur == null) {
            return null;
        }

        return RealisateurMapper.convertRealisateurToRealisateurDTO(realisateur);
    }

    @Override
    public FilmDTO findFilmById(long id) throws ServiceException {
        try {
            Optional<Film> fOptional = filmDAO.findById(id);

            if (fOptional.isEmpty()) {
                return null;
            }

            Film film = fOptional.get();

            return FilmMapper.convertFilmToFilmDTO(film);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<FilmDTO> findFilmByRealisateurId(long id) throws ServiceException {
        try {
            List<Film> films = filmDAO.findByRealisateurId(id);
            return FilmMapper.convertFilmToFilmDTOs(films);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteFilm(long id) throws ServiceException {
        try {
            Optional<Film> fOptional = filmDAO.findById(id);

            if (fOptional.isPresent()) {
                Film film = fOptional.get();
                filmDAO.delete(film);

                Realisateur realisateur = film.getRealisateur();
                List<Film> filmRealises = filmDAO.findByRealisateurId(realisateur.getId());
                realisateur.setFilmRealises(filmRealises);
                this.updateRealisateurCelebre(realisateur);
                realisateurDAO.update(realisateur);
            }
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public FilmDTO updateFilm(long id, FilmForm filmForm) throws ServiceException {
        try {
            if (id <= 0) {
                throw new ServiceException("L'id doit être un entier strictement positif");
            }

            Optional<Film> fOptional = filmDAO.findById(id);
            if (fOptional.isEmpty()) {
                throw new ServiceException("Ce film n'existe pas");
            }
            Film filmOld = fOptional.get();

            Optional<Realisateur> rOptional = realisateurDAO.findById(filmOld.getRealisateur().getId());
            if (rOptional.isEmpty()) {
                throw new ServiceException("Realisateur not found");
            }
            Realisateur realisateurOld = rOptional.get();

            Film film = FilmMapper.convertFilmFormToFilm(filmForm);
            film.setId(filmOld.getId());

            rOptional = realisateurDAO.findById(film.getRealisateur().getId());
            if (rOptional.isEmpty()) {
                throw new ServiceException("Realisateur not found");
            }
            Realisateur realisateur = rOptional.get();
            film.setRealisateur(realisateur);

            film = filmDAO.update(film);

            List<Film> filmRealises = filmDAO.findByRealisateurId(realisateur.getId());
            realisateur.setFilmRealises(filmRealises);
            this.updateRealisateurCelebre(realisateur);
            realisateurDAO.update(realisateur);

            filmRealises = filmDAO.findByRealisateurId(realisateurOld.getId());
            realisateurOld.setFilmRealises(filmRealises);
            this.updateRealisateurCelebre(realisateurOld);
            realisateurDAO.update(realisateurOld);

            return FilmMapper.convertFilmToFilmDTO(film);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
