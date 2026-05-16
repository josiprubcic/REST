package hr.fer.rest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RezervacijaDTO {
    private Long idRezervacija;
    private Integer idCentar;
    private String idTeren;
    private String nazivCentra;
    private String ime;
    private String prezime;
    private LocalDateTime vrijemePocetka;
    private LocalDateTime vrijemeZavrsetka;
}