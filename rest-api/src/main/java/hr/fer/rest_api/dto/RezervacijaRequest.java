package hr.fer.rest_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RezervacijaRequest {
    @NotNull(message = "Korisnik je obavezan")
    private Integer idKorisnika;

    @NotNull(message = "Sportski centar je obavezan")
    private Integer idCentra;

    @NotBlank(message = "Teren je obavezan")
    private String idTerena;

    @NotNull(message = "Vrijeme pocetka je obavezno")
    private LocalDateTime vrijemePocetka;

    @NotNull(message = "Vrijeme zavrsetka je obavezno")
    private LocalDateTime vrijemeZavrsetka;
}