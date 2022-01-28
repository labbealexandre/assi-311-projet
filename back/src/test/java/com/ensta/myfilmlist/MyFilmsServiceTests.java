package com.ensta.myfilmlist;

import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;
import com.ensta.myfilmlist.service.MyFilmsService;
import com.ensta.myfilmlist.service.impl.MyFilmsServiceImpl;

/**
 * Tests des services de l'application MyFilms.
 */
@ExtendWith(MockitoExtension.class)
class MyFilmsServiceTests {

	/** Service MyFilms contenant des Mocks de FilmDAO et RealisateurDAO */
	@InjectMocks
	private MyFilmsService myFilmsServiceMock = new MyFilmsServiceImpl();

	@Mock
	private FilmDAO filmDAOMock;

	@Mock
	private RealisateurDAO realisateurDAOMock;

	/** Teste le calcul de la duree totale des films */
	@Test
	public void calculerDureeTotaleTest() {
		List<Film> films = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		int total = myFilmsServiceMock.calculerDureeTotale(films);

		Assertions.assertEquals(558, total);
	}

	/** Teste le calcul de la duree totale d'une liste vide de films */
	@Test
	public void calculerDureeTotaleEmptyListTest() {
		List<Film> films = Collections.emptyList();
		int total = myFilmsServiceMock.calculerDureeTotale(films);

		Assertions.assertEquals(0, total);
	}

	/** Teste le calcul de la note moyenne */
	@Test
	public void calculerNoteMoyenneTest() {
		double[] notes = { 18, 15.5, 12 };

		double noteMoyenne = myFilmsServiceMock.calculerNoteMoyenne(notes);

		Assertions.assertEquals(15.17, noteMoyenne);
	}

	/** Teste le calcul de la note moyenne d'une liste vide */
	@Test
	public void calculerNoteMoyenneNoNoteTest() {
		double[] notes = {};

		double noteMoyenne = myFilmsServiceMock.calculerNoteMoyenne(notes);

		Assertions.assertEquals(0, noteMoyenne);
	}

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() throws ServiceException {
		List<Film> mockFilms = List.of(getAvatarFilm(), getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(),
				getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findAll()).thenReturn(mockFilms);

		List<FilmDTO> films = myFilmsServiceMock.findAllFilms();
		Mockito.verify(filmDAOMock).findAll();

		Assertions.assertNotNull(films);
		Assertions.assertEquals(4, films.size());

		for (FilmDTO film : films) {
			Assertions.assertNotNull(film);
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertTrue(film.getDuree() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertTrue(film.getRealisateur().getId() > 0);
			Assertions.assertNotNull(film.getRealisateur().getNom());
		}
	}

	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findAllFilmsEmptyTest() throws ServiceException {
		Mockito.when(filmDAOMock.findAll()).thenReturn(new ArrayList<Film>());

		List<FilmDTO> films = myFilmsServiceMock.findAllFilms();
		Mockito.verify(filmDAOMock).findAll();

		Assertions.assertNotNull(films);
		Assertions.assertEquals(0, films.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findAllFilmsExceptionTest() throws ServiceException {
		Mockito.when(filmDAOMock.findAll()).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findAllFilms();
		});
		Mockito.verify(filmDAOMock).findAll();
	}

	/** Teste la creation d'un film */
	@Test
	public void createFilmTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId()))
				.thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenReturn(expectedFilmMock);
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId()))
				.thenReturn(List.of(getAvatarFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut
		// celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class)))
				.thenAnswer(invocation -> invocation.getArguments()[0]);

		FilmDTO filmDTO = myFilmsServiceMock.createFilm(filmFormMock);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));

		Assertions.assertNotNull(filmDTO);
		Assertions.assertEquals(expectedFilmMock.getId(), filmDTO.getId());
		Assertions.assertEquals(expectedFilmMock.getDuree(), filmDTO.getDuree());
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getNom(), filmDTO.getRealisateur().getNom());
		Assertions.assertFalse(filmDTO.getRealisateur().isCelebre());
	}

	/** Teste la creation d'un film sans realisateur */
	@Test
	public void createFilmNoRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(0);

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(filmDAOMock, never()).save(Mockito.any(Film.class));
	}

	/** Teste la creation d'un film avec un realisateur qui n'existe pas */
	@Test
	public void createFilmNotExistingRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(404);
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.empty());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(realisateurDAOMock).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock, never()).save(Mockito.any(Film.class));
	}

	/**
	 * Teste la creation d'un film qui met a jour le statut celebre d'un realisateur
	 */
	@Test
	public void createFilmStatutCelebreRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId()))
				.thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenReturn(expectedFilmMock);
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId()))
				.thenReturn(List.of(getAvatarFilm(), getTitanicFilm(), getTerminatorFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut
		// celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class)))
				.thenAnswer(invocation -> invocation.getArguments()[0]);

		FilmDTO filmDTO = myFilmsServiceMock.createFilm(filmFormMock);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));
		Mockito.verify(realisateurDAOMock).update(Mockito.any(Realisateur.class));

		Assertions.assertNotNull(filmDTO);
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertTrue(filmDTO.getRealisateur().isCelebre());
	}

	/** Teste la creation d'un film qui renvoie une exception */
	@Test
	public void createFilmExceptionTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId()))
				.thenReturn(Optional.of(getJamesCameronRealisateur()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));
	}

	/** Teste la recuperation d'un film a partir de son id */
	@Test
	public void findFilmByIdTest() throws ServiceException {
		long filmId = 1;
		Film expectedFilmMock = getAvatarFilm();
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(expectedFilmMock));

		FilmDTO filmDTO = myFilmsServiceMock.findFilmById(filmId);
		Mockito.verify(filmDAOMock).findById(filmId);

		Assertions.assertNotNull(filmDTO);
		Assertions.assertEquals(expectedFilmMock.getId(), filmDTO.getId());
		Assertions.assertEquals(expectedFilmMock.getDuree(), filmDTO.getDuree());
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getNom(), filmDTO.getRealisateur().getNom());
	}

	/** Teste la recuperation d'un film qui n'existe pas */
	@Test
	public void findFilmByIdNotExisitingTest() throws ServiceException {
		long filmId = 404;
		FilmDTO film = myFilmsServiceMock.findFilmById(filmId);

		Assertions.assertNull(film);
	}

	/** Teste la recuperation d'un film qui renvoie une exception */
	@Test
	public void findFilmByIdExceptionTest() throws ServiceException {
		long filmId = 1;
		Mockito.when(filmDAOMock.findById(filmId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findFilmById(filmId);
		});
		Mockito.verify(filmDAOMock).findById(filmId);
	}

	/** Teste la suppression d'un film */
	@Test
	public void deleteFilmTest() throws ServiceException {
		long filmId = 1;
		Film filmMock = getAvatarFilm();
		Realisateur realisateur = filmMock.getRealisateur();
		realisateur.setCelebre(true);

		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(filmMock));
		Mockito.when(filmDAOMock.findByRealisateurId(filmMock.getRealisateur().getId()))
				.thenReturn(List.of(getTitanicFilm(), getTerminatorFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut
		// celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class)))
				.thenAnswer(invocation -> invocation.getArguments()[0]);

		myFilmsServiceMock.deleteFilm(filmId);
		Mockito.verify(filmDAOMock).delete(filmMock);
		Mockito.verify(realisateurDAOMock).update(Mockito.any(Realisateur.class));

		Assertions.assertFalse(realisateur.isCelebre());
	}

	/** Teste la suppression d'un film qui n'existe pas */
	@Test
	public void deleteFilmNotExisitingTest() throws ServiceException {
		long filmId = 404;
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.empty());

		myFilmsServiceMock.deleteFilm(filmId);

		Mockito.verify(filmDAOMock, never()).delete(Mockito.any(Film.class));
	}

	/** Teste la suppression d'un film qui renvoie une exception */
	@Test
	public void deleteFilmExceptionTest() throws ServiceException {
		long filmId = 1;
		Film filmMock = getAvatarFilm();
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(filmMock));
		// La methode delete() renvoie void, on simule le fait qu'elle leve une
		// exception
		Mockito.doThrow(getDataAccessException()).when(filmDAOMock).delete(filmMock);

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.deleteFilm(filmId);
		});
		Mockito.verify(filmDAOMock).delete(filmMock);
	}

	/** Teste la recuperation des realisateurs */
	@Test
	public void findAllRealisateurTest() throws ServiceException {
		List<Realisateur> realisateurMocks = List.of(getJamesCameronRealisateur(), getPeterJacksonRealisateur());

		Mockito.when(realisateurDAOMock.findAll()).thenReturn(realisateurMocks);

		List<RealisateurDTO> realisateurDTOs = myFilmsServiceMock.findAllRealisateurs();
		Mockito.verify(realisateurDAOMock).findAll();

		Assertions.assertEquals(2, realisateurDTOs.size());
		for (int i = 0; i < 2; i++) {
			Realisateur realisateur = realisateurMocks.get(i);
			RealisateurDTO realisateurDTO = realisateurDTOs.get(i);

			Assertions.assertEquals(realisateur.getId(), realisateurDTO.getId());
			Assertions.assertEquals(realisateur.getPrenom(), realisateurDTO.getPrenom());
			Assertions.assertEquals(realisateur.getNom(), realisateurDTO.getNom());
			Assertions.assertEquals(realisateur.getDateNaissance(), realisateurDTO.getDateNaissance());
		}
	}

	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findAllRealisateurEmptyTest() throws ServiceException {
		Mockito.when(realisateurDAOMock.findAll()).thenReturn(new ArrayList<Realisateur>());

		List<RealisateurDTO> realisateurs = myFilmsServiceMock.findAllRealisateurs();
		Mockito.verify(realisateurDAOMock).findAll();

		Assertions.assertNotNull(realisateurs);
		Assertions.assertEquals(0, realisateurs.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findAllRealisateurExceptionTest() throws ServiceException {
		Mockito.when(realisateurDAOMock.findAll()).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findAllRealisateurs();
		});
		Mockito.verify(realisateurDAOMock).findAll();
	}

	private DataAccessException getDataAccessException() {
		return new EmptyResultDataAccessException(1);
	}

	private Realisateur getJamesCameronRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private Realisateur getPeterJacksonRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(2);
		realisateur.setNom("Jackson");
		realisateur.setPrenom("Peter");
		realisateur.setDateNaissance(LocalDate.of(1961, 10, 31));
		return realisateur;
	}

	private Film getAvatarFilm() {
		Film film = new Film();
		film.setId(1);
		film.setTitre("Avatar");
		film.setDuree(162);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

	private Film getLaCommunauteDeLAnneauFilm() {
		Film film = new Film();
		film.setId(2);
		film.setTitre("La communauté de l'anneau");
		film.setDuree(178);
		film.setRealisateur(getPeterJacksonRealisateur());
		return film;
	}

	private Film getLesDeuxToursFilm() {
		Film film = new Film();
		film.setId(3);
		film.setTitre("Les deux tours");
		film.setDuree(179);
		film.setRealisateur(getPeterJacksonRealisateur());
		return film;
	}

	private Film getLeRetourDuRoiFilm() {
		Film film = new Film();
		film.setId(4);
		film.setTitre("Le retour du roi");
		film.setDuree(201);
		film.setRealisateur(getPeterJacksonRealisateur());
		return film;
	}

	private Film getTitanicFilm() {
		Film film = new Film();
		film.setId(5);
		film.setTitre("Titanic");
		film.setDuree(195);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

	private Film getTerminatorFilm() {
		Film film = new Film();
		film.setId(6);
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

	private FilmForm getTerminatorFilmForm() {
		FilmForm filmForm = new FilmForm();
		filmForm.setTitre("Terminator");
		filmForm.setDuree(107);
		filmForm.setRealisateurId(getJamesCameronRealisateur().getId());
		return filmForm;
	}

}
