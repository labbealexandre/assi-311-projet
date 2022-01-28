package com.ensta.myfilmlist.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Contient les donnees d'un Réalisateur.
 */
public class RealisateurDTO {

    private long id;

    private String nom;

    private String prenom;

    private LocalDate dateNaissance;

    private List<FilmDTO> filmRealises;

    private boolean celebre;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public List<FilmDTO> getFilmRealises() {
        return this.filmRealises;
    }

    public void setFilmRealises(List<FilmDTO> filmRealises) {
        this.filmRealises = filmRealises;
    }

    public boolean isCelebre() {
        return this.celebre;
    }

    public boolean getCelebre() {
        return this.celebre;
    }

    public void setCelebre(boolean celebre) {
        this.celebre = celebre;
    }

    @Override
    public String toString() {
        return "RealisateurDTO [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", dateNaissance=" + dateNaissance
                + ", celebre=" + celebre + "]";
    }
}
