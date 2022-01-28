import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { getFilmById, getRealisateurByNomPrenom, minutesToHours } from 'src/app/external/functions';
import { Film } from 'src/app/models/film';
import { Realisateur } from 'src/app/models/realisateur';
import { FilmService } from 'src/app/services/film.service';

@Component({
  selector: 'app-film-details',
  templateUrl: './film-details.component.html',
  styleUrls: ['./film-details.component.css']
})
export class FilmDetailsComponent implements OnInit {

  id: number = -1;
  exists: Boolean = false;
  realisateur: Realisateur = {
    id: -1,
    nom: "",
    prenom: "",
    dateNaissance: "",
    filmRealises: [],
    celebre: false
  };
  film: Film = {
    id: -1,
    titre: "",
    duree: -1,
    realisateur: this.realisateur
  };
  dureeFormattee: string = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService
  ) { }

  formatteDuree(duree: number): string {
    const [hours, minutes] = minutesToHours(this.film.duree);
    return `${hours}h${minutes}`;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next: (params: ParamMap) => {
        this.id = +params.get('id')!;

        if (isNaN(this.id)) {
          this.router.navigate(["my-films"]);
        }

        this.filmService
          .fetchOneFilmById(this.id)
          .subscribe((film: Film) => {
            this.film = film || this.film;
            this.exists = Boolean(film);

            if (this.exists) {
              const realisateur = this.film.realisateur;
              this.dureeFormattee = this.formatteDuree(this.film.duree);

              this.filmService
                .fetchFilmsByRealisateurId(realisateur.id)
                .subscribe((films: Film[]) => {
                  realisateur.filmRealises = films
                    .filter((film) => film.id != this.film.id);
                  this.realisateur = realisateur;
                })
            }
          })

        // const realisateur = getRealisateurByNomPrenom(this.film.realisateur.nom, this.film.realisateur.prenom);
        // this.realisateur = realisateur || this.realisateur;
        // this.realisateur.filmRealises = this.realisateur.filmRealises.filter((film) => film.id != this.film.id);
      }
    });
  }
}
