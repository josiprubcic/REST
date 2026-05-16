package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.mapper.RezervacijaMapper;
import hr.fer.rest_api.model.Rezervacija;
import hr.fer.rest_api.service.RezervacijaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rezervacije")
@RequiredArgsConstructor
public class RezervacijaController {

    private final RezervacijaService rezervacijaService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody RezervacijaRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(rezervacijaService.createReservation(request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
