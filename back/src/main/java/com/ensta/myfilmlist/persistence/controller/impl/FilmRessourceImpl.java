package com.ensta.myfilmlist.persistence.controller.impl;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Produces;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.service.MyFilmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/film")
@Validated
@Api(tags = "Film")
@Tag(name = "Film", description = "Opération sur les films")
@CrossOrigin(origins = "http://localhost:4200")
public class FilmRessourceImpl {
    @Autowired
    private MyFilmsService myFilmsService;

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Lister les films", notes = "Permet de renvoyer la liste de tous les films.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "La liste des films a été renvoyée correctement") })
    ResponseEntity<List<FilmDTO>> getAllFilms() throws ControllerException {
        try {
            List<FilmDTO> films = myFilmsService.findAllFilms();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(films);

        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupérer un film", notes = "Permet de renvoyer un film pour un id donné.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le film correspondant a été trouvé")
    })
    ResponseEntity<FilmDTO> getFilmById(@PathVariable long id) throws ControllerException {
        try {
            if (id <= 0) {
                throw new ControllerException("L'id doit être un entier strictement positif");
            }

            FilmDTO film = myFilmsService.findFilmById(id);
            HttpStatus status = film == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;

            return ResponseEntity
                    .status(status)
                    .body(film);

        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }

    @GetMapping("/realisateur/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupérer un film", notes = "Permet de renvoyer un film pour un id de réalisateur donné.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le film correspondant a été trouvé")
    })
    ResponseEntity<List<FilmDTO>> getFilmByRealisateurId(@PathVariable long id)
            throws ControllerException {
        try {
            if (id <= 0) {
                throw new ControllerException("L'id doit être un entier strictement positif");
            }

            List<FilmDTO> films = myFilmsService.findFilmByRealisateurId(id);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(films);

        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }

    @PostMapping()
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Créer un film", notes = "Permet de créer un film.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Le film a été correctement ajouté")
    })
    ResponseEntity<FilmDTO> createFilm(@Valid @RequestBody FilmForm filmForm) throws ControllerException {
        try {
            FilmDTO film = myFilmsService.createFilm(filmForm);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(film);

        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }

    @DeleteMapping()
    @ApiOperation(value = "Supprimer un film", notes = "Permet de supprimer un film pour un id donné.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Le film a été correctement supprimé")
    })
    ResponseEntity<Void> deleteFilm(@RequestParam long id) throws ControllerException {
        try {
            myFilmsService.deleteFilm(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }

    @PutMapping()
    @ApiOperation(value = "Modifier un film", notes = "Permet de modifier un film.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le film a été correctement modifié")
    })
    ResponseEntity<FilmDTO> updateFilm(@RequestParam long id, @Valid @RequestBody FilmForm filmForm)
            throws ControllerException {
        try {
            FilmDTO film = myFilmsService.updateFilm(id, filmForm);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(film);
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage());
        }
    }
}
