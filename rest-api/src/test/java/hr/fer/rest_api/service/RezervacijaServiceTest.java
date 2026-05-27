package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.model.*;
import hr.fer.rest_api.repository.RezervacijaRepository;
import hr.fer.rest_api.repository.SportasRekreativacRepository;
import hr.fer.rest_api.repository.TerenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RezervacijaServiceTest {

    @Mock
    private RezervacijaRepository rezervacijaRepository;

    @Mock
    private SportasRekreativacRepository sportasRekreativacRepository;

    @Mock
    private TerenRepository terenRepository;

    @InjectMocks
    private RezervacijaService rezervacijaService;

    @Test
    void shouldCreateAndSaveReservation() {
        LocalDateTime fiksniPocetak = LocalDateTime.of(2026, 5, 5, 14, 0);
        LocalDateTime fiksniZavrsetak = fiksniPocetak.plusHours(1);

        RezervacijaRequest request = new RezervacijaRequest();
        request.setIdKorisnik(1);
        request.setIdCentar(1);
        request.setIdTeren("Teren 1");
        request.setVrijemePocetka(fiksniPocetak);
        request.setVrijemeZavrsetka(fiksniZavrsetak);

        SportasRekreativac rekreativac = new SportasRekreativac();
        rekreativac.setIdKorisnik(1);

        SportskiCentar centar = new SportskiCentar();
        centar.setIdCentar(1);

        Teren teren = new Teren();
        teren.setId(new TerenId(1, "Teren 1"));
        teren.setSportskiCentar(centar);

        when(sportasRekreativacRepository.findById(1)).thenReturn(Optional.of(rekreativac));
        when(terenRepository.findById(any(TerenId.class))).thenReturn(Optional.of(teren));

        when(rezervacijaRepository.existsOverlappingReservation(
                eq(1),
                eq("Teren 1"),
                eq(fiksniPocetak),
                eq(fiksniZavrsetak)
        )).thenReturn(false);

        when(rezervacijaRepository.save(any(Rezervacija.class))).thenAnswer(returnsFirstArg());

        RezervacijaDTO rezultat = rezervacijaService.createReservation(request);

        assertThat(rezultat).isNotNull();
        assertThat(rezultat.getIdCentar()).isEqualTo(request.getIdCentar());
        assertThat(rezultat.getIdTeren()).isEqualTo(request.getIdTeren());
    }

    @Test
    void shouldThrowExceptionForConflictingReservations() {
        LocalDateTime fiksniPocetak = LocalDateTime.of(2026, 5, 5, 14, 0);
        LocalDateTime fiksniZavrsetak = fiksniPocetak.plusHours(1);

        RezervacijaRequest request = new RezervacijaRequest();
        request.setIdKorisnik(1);
        request.setIdCentar(1);
        request.setIdTeren("Teren 1");
        request.setVrijemePocetka(fiksniPocetak);
        request.setVrijemeZavrsetka(fiksniZavrsetak);

        when(sportasRekreativacRepository.findById(1)).thenReturn(Optional.of(new SportasRekreativac()));
        when(terenRepository.findById(any(TerenId.class))).thenReturn(Optional.of(new Teren()));

        when(rezervacijaRepository.existsOverlappingReservation(
                eq(1),
                eq("Teren 1"),
                eq(fiksniPocetak),
                eq(fiksniZavrsetak)
        )).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            rezervacijaService.createReservation(request);
        });
    }
}