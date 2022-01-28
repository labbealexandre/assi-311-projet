package com.ensta.myfilmlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.service.MyFilmsService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests des services web de l'application MyFilms.
 */
@WebMvcTest
@AutoConfigureMockMvc
public class MyFilmsControllerTests {

	/** Permet de simuler les appels HTTP */
	@Autowired
	private MockMvc mockMvc;

	/** Mock du bean Spring correspondant au service */
	@MockBean
	private MyFilmsService myFilmsServiceMock;

	/** Permet de convertir des objets en JSON */
	@Autowired
	private ObjectMapper objectMapper;

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() throws Exception {
		List<FilmDTO> films = List.of(getAvatarFilm(), getLaCommunauteDeLAnneauFilm());
		Mockito.when(myFilmsServiceMock.findAllFilms()).thenReturn(films);

		final String expectedResponseContent = objectMapper.writeValueAsString(films);

		mockMvc.perform(MockMvcRequestBuilders.get("/film"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findAllFilms();
	}

	/**
	 * Teste la recuperation des films lorsqu'une exception est renvoyee par la
	 * couche service
	 */
	@Test
	public void findAllFilmsServiceExceptionTest() throws Exception {
		Mockito.when(myFilmsServiceMock.findAllFilms()).thenThrow(new ServiceException("findAllFilmsExceptionTest"));

		mockMvc.perform(MockMvcRequestBuilders.get("/film"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		Mockito.verify(myFilmsServiceMock).findAllFilms();
	}

	/** Teste la recuperation d'un film par son id */
	@Test
	public void getFilmByIdTest() throws Exception {
		long filmId = 1;
		FilmDTO expectedFilm = getAvatarFilm();
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenReturn(expectedFilm);

		final String expectedResponseContent = objectMapper.writeValueAsString(expectedFilm);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}

	/** Teste la recuperation d'un film qui n'existe pas par son id */
	@Test
	public void getFilmByIdNotExisitingTest() throws Exception {
		long filmId = 404;
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}

	/**
	 * Teste la recuperation d'un film avec un id qui vaut 0 : doit renvoyer une
	 * erreur 400
	 */
	@Test
	public void getFilmByIdZeroTest() throws Exception {
		long filmId = 0;

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	/**
	 * Teste le handler par defaut en cas d'Exception : on doit renvoyer une reponse
	 * valide a l'utilisateur
	 */
	@Test
	public void getFilmByIdHandleExceptionTest() throws Exception {
		long filmId = 1;
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenThrow(RuntimeException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}

	/** Teste la creation d'un film */
	@Test
	public void createFilmTest() throws Exception {
		FilmDTO expectedFilm = getTerminatorFilm();
		Mockito.when(myFilmsServiceMock.createFilm(Mockito.any(FilmForm.class))).thenReturn(expectedFilm);

		FilmForm filmForm = getTerminatorFilmForm();
		final String paramsContent = objectMapper.writeValueAsString(filmForm);

		ArgumentCaptor<FilmForm> argument = ArgumentCaptor.forClass(FilmForm.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		Mockito.verify(myFilmsServiceMock).createFilm(argument.capture());

		assertEquals(filmForm.getTitre(), argument.getValue().getTitre());
		assertEquals(filmForm.getDuree(), argument.getValue().getDuree());
		assertEquals(filmForm.getRealisateurId(), argument.getValue().getRealisateurId());
	}

	/** Teste la creation d'un film avec un titre vide */
	@Test
	public void createFilmTitreBlankTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film avec une duree negative */
	@Test
	public void createFilmDureeNegativeTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(-1);
		filmFormParams.setRealisateurId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film avec un realisateurId qui vaut 0 */
	@Test
	public void createFilmRealisateurId0Test() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(0);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film qui leve une ServiceException */
	@Test
	public void createFilmExceptionTest() throws Exception {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.when(myFilmsServiceMock.createFilm(Mockito.any(FilmForm.class)))
				.thenThrow(new ServiceException("createFilmExceptionTest"));

		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre(filmFormMock.getTitre());
		filmFormParams.setDuree(filmFormMock.getDuree());
		filmFormParams.setRealisateurId(filmFormMock.getRealisateurId());
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la suppression d'un film par son id */
	@Test
	public void deleteFilmTest() throws Exception {
		long filmId = 1;
		mockMvc.perform(MockMvcRequestBuilders.delete("/film?id={id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		Mockito.verify(myFilmsServiceMock).deleteFilm(filmId);
	}

	private RealisateurDTO getJamesCameronRealisateur() {
		RealisateurDTO realisateur = new RealisateurDTO();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private RealisateurDTO getPeterJacksonRealisateur() {
		RealisateurDTO realisateur = new RealisateurDTO();
		realisateur.setId(2);
		realisateur.setNom("Jackson");
		realisateur.setPrenom("Peter");
		realisateur.setDateNaissance(LocalDate.of(1961, 10, 31));
		return realisateur;
	}

	private FilmDTO getAvatarFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(1);
		film.setTitre("Avatar");
		film.setDuree(162);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

	private FilmDTO getLaCommunauteDeLAnneauFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(2);
		film.setTitre("La communauté de l'anneau");
		film.setDuree(178);
		film.setRealisateur(getPeterJacksonRealisateur());
		return film;
	}

	private FilmDTO getTerminatorFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(6);
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

	private FilmForm getTerminatorFilmForm() {
		FilmForm film = new FilmForm();
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateurId(getJamesCameronRealisateur().getId());
		return film;
	}
}
