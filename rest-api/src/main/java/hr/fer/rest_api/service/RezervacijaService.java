package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.dto.SlobodniTerminDTO;
import hr.fer.rest_api.mapper.RezervacijaMapper;
import hr.fer.rest_api.model.*;
import hr.fer.rest_api.repository.RezervacijaRepository;
import hr.fer.rest_api.repository.SportasRekreativacRepository;
import hr.fer.rest_api.repository.SportskiCentarRepository;
import hr.fer.rest_api.repository.TerenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RezervacijaService {

    private final RezervacijaRepository rezervacijaRepository;
    private final SportasRekreativacRepository sportasRekreativacRepository;
    private final TerenRepository terenRepository;
    private final SportskiCentarRepository sportskiCentarRepository;

    public RezervacijaDTO createReservation(RezervacijaRequest request) {
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

        return RezervacijaMapper.toDto(rezervacijaRepository.save(rezervacija));
    }

    @Transactional(readOnly = true)
    public List<SlobodniTerminDTO> getAvailableTermini(Integer idCentar, LocalDate datum) {

        SportskiCentar centar = sportskiCentarRepository.findById(idCentar)
                .orElseThrow(() -> new RuntimeException("Sportski centar not found"));
        LocalTime radnoOd;
        LocalTime radnoDo;
        DayOfWeek danUtjednu = datum.getDayOfWeek();

        if (danUtjednu == DayOfWeek.SATURDAY || danUtjednu == DayOfWeek.SUNDAY) {
            radnoOd = centar.getRadnoVrijemeVikendOd();
            radnoDo = centar.getRadnoVrijemeVikendDo();
        } else {
            radnoOd = centar.getRadnoVrijemeTjedanOd();
            radnoDo = centar.getRadnoVrijemeTjedanDo();
        }
        LocalDateTime pocetakDana = datum.atStartOfDay();
        LocalDateTime krajDana = datum.atTime(LocalTime.MAX);
        List<Rezervacija> zauzeteRezervacije = rezervacijaRepository.findZauzeteRezervacije(idCentar, pocetakDana, krajDana);
        System.out.println(zauzeteRezervacije);

        List<Teren> sviTereni = terenRepository.findBySportskiCentarIdCentar(idCentar);
        List<SlobodniTerminDTO> sviSlobodniTermini = new ArrayList<>();

        for (Teren teren : sviTereni) {
            LocalTime tekuciSat = radnoOd;

            while (tekuciSat.isBefore(radnoDo)) {
                LocalDateTime slotPocetak = datum.atTime(tekuciSat);
                LocalDateTime slotZavrsetak = slotPocetak.plusHours(1);

                boolean jeZauzet = false;
                for (Rezervacija rez : zauzeteRezervacije) {
                    if (rez.getTeren().getId().equals(teren.getId()) && rez.getVrijemePocetka().equals(slotPocetak)) {
                        jeZauzet = true;
                        break;
                    }
                }
                if (!jeZauzet) {
                    SlobodniTerminDTO slobodanSlot = new SlobodniTerminDTO(
                            teren.getId().getIdTeren(),
                            slotPocetak,
                            slotZavrsetak
                    );
                    sviSlobodniTermini.add(slobodanSlot);
                }
                tekuciSat = tekuciSat.plusHours(1);
            }
        }
        return sviSlobodniTermini;
    }
}
