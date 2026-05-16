package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.SportskiCentar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportskiCentarRepository extends JpaRepository<SportskiCentar, Integer> {

    List<SportskiCentar> findByNazivCentraContainingIgnoreCase(String search);

    List<SportskiCentar> findByMjesto_IdMjesto(Integer idMjesto);

    boolean existsByMjesto_IdMjesto(Integer idMjesto);

    List<SportskiCentar> findByNazivCentraContainingIgnoreCaseAndMjesto_IdMjesto(
            String search,
            Integer idMjesto
    );

    boolean existsByAdresaIgnoreCaseAndMjesto_IdMjesto(
            String adresa,
            Integer idMjesto
    );

    boolean existsByAdresaIgnoreCaseAndMjesto_IdMjestoAndIdCentarNot(
            String adresa,
            Integer idMjesto,
            Integer idCentar
    );
}
