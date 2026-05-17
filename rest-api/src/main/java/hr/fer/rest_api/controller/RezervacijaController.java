package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.mapper.RezervacijaMapper;
import hr.fer.rest_api.model.Rezervacija;
import hr.fer.rest_api.service.RezervacijaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rezervacije")
@RequiredArgsConstructor
public class RezervacijaController {

    private final RezervacijaService rezervacijaService;

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableTermini(@RequestParam Integer idCentar,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return ResponseEntity.status(HttpStatus.OK).body(rezervacijaService.getAvailableTermini(idCentar, datum));
    }

    @GetMapping
    public ResponseEntity<?> getReservationsForUser(@RequestParam Integer idKorisnik) {
        return ResponseEntity.status(HttpStatus.OK).body(rezervacijaService.getByIdKorisnik(idKorisnik));
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody RezervacijaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rezervacijaService.createReservation(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> otkaziRezervaciju(@PathVariable("id") Long idRezervacija) {
        try {
            boolean canceled = rezervacijaService.cancelReservation(idRezervacija);
            if (canceled) {
                return ResponseEntity.ok("Rezervacija uspješno otkazana.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rezervacija:" + idRezervacija + " ne postoji.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška na poslužitelju: " + e.getMessage());
        }
    }



}
