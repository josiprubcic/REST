package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportRepository extends JpaRepository<Sport, Integer> {

    List<Sport> findByNazivSportaContainingIgnoreCase(String search);

    boolean existsByNazivSportaIgnoreCase(String nazivSporta);

    boolean existsByNazivSportaIgnoreCaseAndIdSportNot(String nazivSporta, Integer idSport);


}
