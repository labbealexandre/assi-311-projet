import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Film, FilmForm } from '../models/film';

@Injectable({
  providedIn: 'root'
})
export class FilmService {

  url: string = 'http://localhost:8080';
  films: Film[] = [];
  filmsSubject: Subject<Film[]> = new Subject<Film[]>();

  constructor(private http: HttpClient) { }

  public fetchAllFilms(): void {
    const requestUrl = `${this.url}/film`
    this.http.get<Film[]>(requestUrl).subscribe((films: Film[]) => {
      this.films = films;
      this.emitFilms();
    });
  }

  public fetchOneFilmById(id: number): Observable<Film> {
    const requestUrl = `${this.url}/film/${id}`
    return this.http.get<Film>(requestUrl);
  }

  public fetchFilmsByRealisateurId(id: number): Observable<Film[]> {
    const requestUrl = `${this.url}/film/realisateur/${id}`;
    return this.http.get<Film[]>(requestUrl);
  }

  public createFilm(filmForm: FilmForm): void {
    const requestUrl = `${this.url}/film`;
    this.http.post<Film>(requestUrl, filmForm).subscribe((film: Film) => {
      this.films.push(film);
      this.emitFilms();
    });
  }

  public editFilm(id: number, filmForm: FilmForm): void {
    const requestUrl = `${this.url}/film?id=${id}`;
    this.http.put<Film>(requestUrl, filmForm).subscribe((film: Film) => {
      const index = this.films.findIndex(f => f.id === id);
      this.films.splice(index, 1, film);
      this.emitFilms();
    });
  }

  public deleteFilm(id: number): void {
    const requestUrl = `${this.url}/film?id=${id}`;
    this.http.delete(requestUrl).subscribe(() => {
      const index = this.films.findIndex(f => f.id === id);
      this.films.splice(index, 1);
      this.emitFilms();
    })
  }

  public emitFilms(): void {
    this.filmsSubject.next(this.films);
  }
}