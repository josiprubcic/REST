package hr.fer.rest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlobodniTerminDTO {
    private String idTeren;
    private LocalDateTime vrijemePocetka;
    private LocalDateTime vrijemeZavrsetka;
}