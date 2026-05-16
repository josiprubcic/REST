package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.Rezervacija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RezervacijaRepository extends JpaRepository<Rezervacija, Long> {

    List<Rezervacija> findByTerenIdIdCentarAndTerenIdIdTeren(Integer idCentar, String idTeren);

    @Query("SELECT COUNT(r) > 0 FROM Rezervacija r WHERE r.teren.id.idCentar = :idCentar " +
            "AND r.teren.id.idTeren = :idTeren " +
            "AND (:pocetak < r.vrijemeZavrsetka AND :kraj > r.vrijemePocetka)")
    boolean existsOverlappingReservation(@Param("idCentar") Integer idCentar,
                                         @Param("idTeren") String idTeren,
                                         @Param("pocetak") LocalDateTime pocetak,
                                         @Param("kraj") LocalDateTime kraj);
}