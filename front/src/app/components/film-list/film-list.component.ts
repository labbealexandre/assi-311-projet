import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Film } from 'src/app/models/film';
import { FilmService } from 'src/app/services/film.service';

@Component({
  selector: 'app-film-list',
  templateUrl: './film-list.component.html',
  styleUrls: ['./film-list.component.css']
})
export class FilmListComponent implements OnInit {

  films: Film[] = [];
  filmForm!: FormGroup;
  filmsSubcription!: Subscription;
  editMode: Boolean = false;
  currentFilm?: Film;

  constructor(private filmService: FilmService, private formBuilder: FormBuilder) { }

  initFilmForm(): void {
    this.filmForm = this.formBuilder.group({
      titre: ['', Validators.required],
      duree: ['', [Validators.required, Validators.min(1)]],
      realisateurId: ['', [Validators.required, Validators.min(1)]],
    })
  }

  ngOnInit(): void {
    this.filmsSubcription = this.filmService.filmsSubject.subscribe({
      next: (films: Film[]) => {
        this.films = films;
        this.filmForm.reset();
      }
    });

    this.filmService
      .fetchAllFilms();

    this.initFilmForm();
  }

  onCreateUpdateSubmit(): void {
    if (this.editMode) {
      this.filmService
        .editFilm(this.currentFilm?.id || -1, this.filmForm.value)
    } else {
      this.filmService
        .createFilm(this.filmForm.value);
    }
  }

  onDeleteSubmit(): void {
    this.filmService.deleteFilm(this.currentFilm?.id || -1);
  }

  setEditMode(val: Boolean) {
    this.editMode = val;
  }

  edit(id: number) {
    this.setEditMode(true);

    this.currentFilm = this.films.find(f => f.id === id);

    this.filmForm.get('titre')?.setValue(this.currentFilm?.titre);
    this.filmForm.get('duree')?.setValue(this.currentFilm?.duree);
    this.filmForm.get('realisateurId')?.setValue(this.currentFilm?.realisateur.id);
  }

  remove(id: number) {
    this.currentFilm = this.films.find(f => f.id === id);
  }
}
