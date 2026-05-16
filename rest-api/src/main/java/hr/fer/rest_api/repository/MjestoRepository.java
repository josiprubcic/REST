package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.Mjesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MjestoRepository extends JpaRepository<Mjesto, Integer> {

    List<Mjesto> findByNazivMjestaContainingIgnoreCaseOrPostanskiBrojContainingIgnoreCase(
            String nazivMjesta,
            String postanskiBroj
    );

    boolean existsByNazivMjestaIgnoreCaseAndPostanskiBroj(String nazivMjesta, String postanskiBroj);

    boolean existsByNazivMjestaIgnoreCaseAndPostanskiBrojAndIdMjestoNot(
            String nazivMjesta,
            String postanskiBroj,
            Integer idMjesto
    );
}
