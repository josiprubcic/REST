package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.mapper.RezervacijaMapper;
import hr.fer.rest_api.model.Rezervacija;
import hr.fer.rest_api.service.RezervacijaService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> createReservation(@Valid @RequestBody RezervacijaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).
                body(rezervacijaService.createReservation(request));

    }
}
