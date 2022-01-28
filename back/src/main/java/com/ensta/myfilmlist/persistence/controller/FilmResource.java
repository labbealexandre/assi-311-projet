package com.ensta.myfilmlist.persistence.controller;

import java.util.List;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.form.FilmForm;

import org.springframework.http.ResponseEntity;

public interface FilmResource {
    ResponseEntity<List<FilmDTO>> getAllFilms() throws ControllerException;

    ResponseEntity<FilmDTO> getFilmById(long id) throws ControllerException;

    ResponseEntity<FilmDTO> getFilmByRealisateurId(long id) throws ControllerException;

    ResponseEntity<FilmDTO> createFilm(FilmForm filmForm) throws ControllerException;

    ResponseEntity<Void> deleteFilm(long id) throws ControllerException;

    ResponseEntity<FilmDTO> updateFilm(long id, FilmForm filmForm) throws ControllerException;
}