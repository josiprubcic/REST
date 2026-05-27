package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.service.RezervacijaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RezervacijaControllerTest {

    @Mock
    private RezervacijaService rezervacijaService;

    @InjectMocks
    private RezervacijaController rezervacijaController;

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnReservationsForUser() {
        RezervacijaDTO dummyDto = new RezervacijaDTO();
        dummyDto.setIdCentar(1);
        dummyDto.setIdTeren("Teren 1");

        List<RezervacijaDTO> ocekivaneRezervacije = List.of(dummyDto);
        when(rezervacijaService.getByIdKorisnik(1)).thenReturn(ocekivaneRezervacije);

        ResponseEntity<?> response = rezervacijaController.getReservationsForUser(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ocekivaneRezervacije);

        List<RezervacijaDTO> stvarniRezultati = (List<RezervacijaDTO>) response.getBody();
        assertThat(stvarniRezultati).isNotNull();
        assertThat(stvarniRezultati.get(0).getIdTeren()).isEqualTo("Teren 1");
    }
}