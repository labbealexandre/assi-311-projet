import { Realisateur } from "./realisateur";

export interface Film {
    id: number;
    titre: string;
    duree: number;
    realisateur: Realisateur
}

export interface FilmForm {
    titre: string;
    duree: number;
    realisateurId: number;
}
