package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.SportskiCentar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportskiCentarRepository extends JpaRepository<SportskiCentar, Integer> {
}
