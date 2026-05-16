package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.exception.ResourceNotFoundException;
import hr.fer.rest_api.exception.RequestValidationException;
import hr.fer.rest_api.mapper.RezervacijaMapper;
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

    public RezervacijaDTO createReservation(RezervacijaRequest request) {

        validateReservationInterval(request);


        SportasRekreativac rekreativac = sportasRekreativacRepository.findById(request.getIdKorisnika())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));
        TerenId terenId = new TerenId(request.getIdCentra(), request.getIdTerena());
        Teren teren = terenRepository.findById(terenId)
                .orElseThrow(() -> new ResourceNotFoundException("Teren not found"));

        boolean terminReserved = rezervacijaRepository.existsOverlappingReservation(
                request.getIdCentra(),
                request.getIdTerena(),
                request.getVrijemePocetka(),
                request.getVrijemeZavrsetka()
        );
        if (terminReserved) {
            throw new ConflictException("Termin already reserved");
        }

        if (!request.getVrijemePocetka().isBefore(request.getVrijemeZavrsetka())) {
            throw new RequestValidationException("Vrijeme početka mora biti prije vremena završetka");
        }
        Rezervacija rezervacija = new Rezervacija();
        rezervacija.setSportasRekreativac(rekreativac);
        rezervacija.setTeren(teren);
        rezervacija.setVrijemePocetka(request.getVrijemePocetka());
        rezervacija.setVrijemeZavrsetka(request.getVrijemeZavrsetka());


        return RezervacijaMapper.toDto(rezervacijaRepository.save(rezervacija));
    }

    private void validateReservationInterval(RezervacijaRequest request) {
        if(request.getVrijemeZavrsetka() == null || request.getVrijemePocetka() == null){
            throw new RequestValidationException("Vrijeme pocetka i zavrsetka su obavezni");
        }

        if(!request.getVrijemePocetka().isBefore(request.getVrijemeZavrsetka())){
            throw new RequestValidationException("Vrijeme početka mora biti prije vremena zavrsetka");
        }
    }
}
