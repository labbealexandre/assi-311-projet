package com.ensta.myfilmlist.mapper;

import java.util.List;

import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.model.Realisateur;

public class RealisateurMapper {
    public static List<RealisateurDTO> convertRealisateurToRealisateurDTOs(List<Realisateur> realisateurs) {
        return realisateurs.stream().map(RealisateurMapper::convertRealisateurToRealisateurDTO).toList();
    }

    public static RealisateurDTO convertRealisateurToRealisateurDTO(Realisateur realisateur) {
        if (realisateur == null) {
            return null;
        }

        RealisateurDTO realisateurDTO = new RealisateurDTO();
        realisateurDTO.setId(realisateur.getId());
        realisateurDTO.setNom(realisateur.getNom());
        realisateurDTO.setPrenom(realisateur.getPrenom());
        realisateurDTO.setDateNaissance(realisateur.getDateNaissance());
        realisateurDTO.setCelebre(realisateur.getCelebre());

        return realisateurDTO;
    }

    public static Realisateur convertRealisateurDTOToRealisateur(RealisateurDTO realisateurDTO) {
        Realisateur realisateur = new Realisateur();
        realisateur.setId(realisateurDTO.getId());
        realisateur.setNom(realisateurDTO.getNom());
        realisateur.setPrenom(realisateurDTO.getPrenom());
        realisateur.setDateNaissance(realisateur.getDateNaissance());
        realisateur.setCelebre(realisateur.getCelebre());

        return realisateur;
    }
}
