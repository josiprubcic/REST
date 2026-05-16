package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, Integer> {
    Optional<Korisnik> findByEmail(String email);
}