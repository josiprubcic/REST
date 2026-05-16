package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.model.Rezervacija;
import hr.fer.rest_api.model.SportasRekreativac;
import hr.fer.rest_api.model.Teren;
import hr.fer.rest_api.model.TerenId;
import hr.fer.rest_api.repository.RezervacijaRepository;
import hr.fer.rest_api.repository.SportasRekreativacRepository;
import hr.fer.rest_api.repository.TerenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RezervacijaService {

    private final RezervacijaRepository rezervacijaRepository;
    private final SportasRekreativacRepository sportasRekreativacRepository;
    private final TerenRepository terenRepository;

    public Rezervacija createReservation(RezervacijaRequest request) {
        SportasRekreativac rekreativac = sportasRekreativacRepository.findById(request.getIdKorisnika())
                .orElseThrow(() -> new RuntimeException("Korisnik not found"));
        TerenId terenId = new TerenId(request.getIdCentra(), request.getIdTerena());
        Teren teren = terenRepository.findById(terenId)
                .orElseThrow(() -> new RuntimeException("Teren not found"));

        boolean terminReserved = rezervacijaRepository.existsOverlappingReservation(
                request.getIdCentra(),
                request.getIdTerena(),
                request.getVrijemePocetka(),
                request.getVrijemeZavrsetka()
        );
        if (terminReserved) {
            throw new RuntimeException("Termin already reserved");
        }
        Rezervacija rezervacija = new Rezervacija();
        rezervacija.setSportasRekreativac(rekreativac);
        rezervacija.setTeren(teren);
        rezervacija.setVrijemePocetka(request.getVrijemePocetka());
        rezervacija.setVrijemeZavrsetka(request.getVrijemeZavrsetka());

        return rezervacijaRepository.save(rezervacija);
    }
}
