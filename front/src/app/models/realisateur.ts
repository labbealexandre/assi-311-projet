import { Film } from "./film";

export interface Realisateur {
    id: number;
    nom: string;
    prenom: string;
    dateNaissance: string;
    filmRealises: Film[];
    celebre: Boolean;
}
