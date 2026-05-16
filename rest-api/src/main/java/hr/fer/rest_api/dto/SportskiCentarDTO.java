package hr.fer.rest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportskiCentarDTO {
    private Integer idCentar;
    private String nazivCentra;
    private String adresa;
    private Integer idMjesto;
    private String nazivMjesta;
    private Integer idVlasnik;
    private LocalTime radnoVrijemeTjedanOd;
    private LocalTime radnoVrijemeTjedanDo;
    private LocalTime radnoVrijemeVikendOd;
    private LocalTime radnoVrijemeVikendDo;
}
