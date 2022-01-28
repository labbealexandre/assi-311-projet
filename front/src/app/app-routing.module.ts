import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FilmDetailsComponent } from './components/film-details/film-details.component';
import { FilmListComponent } from './components/film-list/film-list.component';
import { NotFoundComponent } from './components/not-found/not-found.component';

const routes: Routes = [
  { path: "my-films", component: FilmListComponent },
  { path: "", redirectTo: "/my-films", pathMatch: "full" },
  { path: "my-films/:id", component: FilmDetailsComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
