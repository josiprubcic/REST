package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.LoginRequest;
import hr.fer.rest_api.model.Korisnik;
import hr.fer.rest_api.repository.KorisnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;

    public Korisnik login(LoginRequest loginRequest) {
        Optional<Korisnik> korisnikOpt = korisnikRepository.findByEmail(loginRequest.getEmail());

        if (korisnikOpt.isPresent()) {
            Korisnik korisnik = korisnikOpt.get();

            if (korisnik.getLozinka().equals(loginRequest.getLozinka())) {
                return korisnik;
            }
        }

        return null;
    }
}