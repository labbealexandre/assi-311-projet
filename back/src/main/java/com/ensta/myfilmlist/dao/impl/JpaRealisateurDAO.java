package com.ensta.myfilmlist.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.model.Realisateur;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class JpaRealisateurDAO implements RealisateurDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Realisateur> findAll() {
        String sql = "SELECT r FROM Realisateur r";

        return entityManager
                .createQuery(sql, Realisateur.class)
                .getResultList();
    }

    public Realisateur findByNomAndPrenom(String nom, String prenom) {
        String sql = String.format("SELECT r FROM Realisateur r WHERE r.nom = %s AND r.prenom = %s", nom, prenom);

        return entityManager
                .createQuery(sql, Realisateur.class)
                .getSingleResult();
    }

    public Optional<Realisateur> findById(long id) {
        Realisateur realisateur = entityManager.find(Realisateur.class, id);
        return Optional.ofNullable(realisateur);
    }

    public Realisateur update(Realisateur realisateur) {
        entityManager.merge(realisateur);
        return entityManager.find(Realisateur.class, realisateur.getId());
    }
}
