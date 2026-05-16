package hr.fer.rest_api.controller;

import hr.fer.rest_api.service.SportskiCentarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sportski/centri")
@RequiredArgsConstructor
public class SportskiCentarController {
    private final SportskiCentarService sportskiCentarService;

    @GetMapping
    public ResponseEntity<?> getSportskiCentri() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sportskiCentarService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
