package com.ensta.myfilmlist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;

/**
 * Tests de la persistance de l'application MyFilms.
 */
@SpringBootTest
@Sql(scripts = { "/clean_data.sql", "/data_test.sql" })
@Transactional
class MyFilmsDAOTests {

	@Autowired
	private FilmDAO filmDAO;

	@Autowired
	private RealisateurDAO realisateurDAO;

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() {
		List<Film> films = filmDAO.findAll();

		Assertions.assertEquals(4, films.size());
	}

	/** Teste la creation d'un film */
	@Test
	public void saveFilmTest() {
		Film filmToSave = getTerminatorFilm();

		Film film = filmDAO.save(filmToSave);
		long filmId = film.getId();
		Assertions.assertNotNull(film);
		Assertions.assertTrue(filmId > 0);

		Assertions.assertTrue(filmDAO.findById(filmId).isPresent());
	}

	/** Teste la recuperation d'un film par son id */
	@Test
	public void findByIdFilmTest() {
		long filmId = 1;
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertFalse(filmOptional.isEmpty());

		Film film = filmOptional.get();

		Assertions.assertEquals(filmId, film.getId());
		Assertions.assertTrue(film.getDuree() > 0);
		Assertions.assertNotNull(film.getRealisateur());
		Assertions.assertTrue(film.getRealisateur().getId() > 0);
		Assertions.assertNotNull(film.getRealisateur().getNom());
	}

	/** Teste la recuperation d'un film par son id qui n'existe pas */
	@Test
	public void findByIdFilmNotExisitingTest() {
		long filmId = 404;
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertTrue(filmOptional.isEmpty());
	}

	/** Teste la suppression d'un film */
	@Test
	public void deleteFilmTest() {
		long filmId = 1;
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertFalse(filmOptional.isEmpty());

		Film film = filmOptional.get();
		filmDAO.delete(film);

		filmOptional = filmDAO.findById(filmId);
		Assertions.assertNotNull(filmOptional);
		Assertions.assertTrue(filmOptional.isEmpty());
	}

	/** Teste la suppression d'un film qui n'existe pas */
	@Test
	public void deleteFilmNotExistingTest() {
		long filmId = 404;
		Film filmToDelete = new Film();
		filmToDelete.setId(filmId);
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertTrue(filmOptional.isEmpty());

		filmDAO.delete(filmToDelete);

		Assertions.assertTrue(filmDAO.findById(filmId).isEmpty());
	}

	/** Teste la recuperation des films par realisateurId */
	@Test
	public void findByRealisateurIdTest() {
		long realisateurId = 2;

		List<Film> films = filmDAO.findByRealisateurId(realisateurId);

		Assertions.assertNotNull(films);
		Assertions.assertEquals(3, films.size());
		for (Film film : films) {
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertEquals(realisateurId, film.getRealisateur().getId());
		}
	}

	/** Teste la recuperation des film par un realisateurId qui n'existe pas */
	@Test
	public void findByRealisateurIdNotExisitingTest() {
		long realisateurId = 404;

		List<Film> films = filmDAO.findByRealisateurId(realisateurId);

		Assertions.assertNotNull(films);
		Assertions.assertTrue(films.isEmpty());
	}

	/** Teste la recuperation des realisateurs */
	@Test
	public void findAllRealisateursTest() {
		List<Film> films = filmDAO.findAll();

		Assertions.assertEquals(4, films.size());
	}

	/** Teste la recuperation d'un realisateur par son id */
	@Test
	public void findByIdRealisateurTest() {
		long realisateurId = 1;
		Optional<Realisateur> realisateurOptional = realisateurDAO.findById(realisateurId);

		Assertions.assertNotNull(realisateurOptional);
		Assertions.assertFalse(realisateurOptional.isEmpty());

		Realisateur realisateur = realisateurOptional.get();

		Assertions.assertEquals(realisateurId, realisateur.getId());
		Assertions.assertNotNull(realisateur.getNom());
		Assertions.assertNotNull(realisateur.getPrenom());
		Assertions.assertNotNull(realisateur.getDateNaissance());
	}

	/** Teste la recuperation d'un realisateur par son id qui n'existe pas */
	@Test
	public void findByIdRealisateurNotExisitingTest() {
		long realisateurId = 404;
		Optional<Realisateur> realisateurOptional = realisateurDAO.findById(realisateurId);

		Assertions.assertNotNull(realisateurOptional);
		Assertions.assertTrue(realisateurOptional.isEmpty());
	}

	/** Teste la mise a jour d'un realisateur */
	@Test
	public void updateRealisateurTest() {
		long realisateurId = 1;
		Realisateur realisateur = realisateurDAO.findById(realisateurId).get();

		realisateur.setPrenom("Jean");
		realisateur.setNom("Dujardin");
		realisateur = realisateurDAO.update(realisateur);

		Assertions.assertEquals("Jean", realisateur.getPrenom());
		Assertions.assertEquals("Dujardin", realisateur.getNom());

		realisateurDAO.findById(realisateurId).get();

		Assertions.assertEquals("Jean", realisateur.getPrenom());
		Assertions.assertEquals("Dujardin", realisateur.getNom());
	}

	private Realisateur getJamesCameronRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private Film getTerminatorFilm() {
		Film film = new Film();
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		return film;
	}

}
