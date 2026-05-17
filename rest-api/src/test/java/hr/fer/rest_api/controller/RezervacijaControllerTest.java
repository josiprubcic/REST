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
    void shouldReturnReservationsForUser() {
        List<RezervacijaDTO> ocekivaneRezervacije = Collections.emptyList();
        when(rezervacijaService.getByIdKorisnik(1)).thenReturn(ocekivaneRezervacije);

        ResponseEntity<?> response = rezervacijaController.getReservationsForUser(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ocekivaneRezervacije);
    }
}