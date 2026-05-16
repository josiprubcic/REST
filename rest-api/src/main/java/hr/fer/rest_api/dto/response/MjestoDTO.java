package hr.fer.rest_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MjestoDTO {
    private Integer idMjesto;
    private String nazivMjesta;
    private String postanskiBroj;
}
