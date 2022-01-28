package com.ensta.myfilmlist.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * Contient les donnees pour requeter un film.
 */
public class FilmForm {

	@NotBlank
	private String titre;

	@Positive
	private int duree;

	@Positive
	private long realisateurId;

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public long getRealisateurId() {
		return realisateurId;
	}

	public void setRealisateurId(long realisateurId) {
		this.realisateurId = realisateurId;
	}

}
