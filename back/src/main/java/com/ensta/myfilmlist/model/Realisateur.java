package com.ensta.myfilmlist.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Représente un Realisateur
 */
@Entity
public class Realisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nom;

    private String prenom;

    private LocalDate dateNaissance;

    @OneToMany(mappedBy = "realisateur")
    private List<Film> filmRealises;

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

    public List<Film> getFilmRealises() {
        return this.filmRealises;
    }

    public void setFilmRealises(List<Film> filmRealises) {
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
}