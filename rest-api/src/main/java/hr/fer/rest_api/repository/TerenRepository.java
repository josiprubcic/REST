package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.Teren;
import hr.fer.rest_api.model.TerenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerenRepository extends JpaRepository<Teren, TerenId> {
    List<Teren> findBySportskiCentarIdCentar(Integer idCentar);
}