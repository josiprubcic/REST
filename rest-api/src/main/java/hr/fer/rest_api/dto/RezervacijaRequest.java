package hr.fer.rest_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RezervacijaRequest {
    private Integer idKorisnika;
    private Integer idCentra;
    private String idTerena;
    private LocalDateTime vrijemePocetka;
    private LocalDateTime vrijemeZavrsetka;
}