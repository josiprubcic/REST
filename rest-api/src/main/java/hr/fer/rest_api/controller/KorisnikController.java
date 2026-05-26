package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.LoginRequest;
import hr.fer.rest_api.model.Korisnik;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KorisnikController {

    private final KorisnikService korisnikService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Korisnik korisnik = korisnikService.login(loginRequest);

        if (korisnik != null) {
            return ResponseEntity.status(HttpStatus.OK).body(korisnik);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neispravan email ili lozinka");
    }

}
