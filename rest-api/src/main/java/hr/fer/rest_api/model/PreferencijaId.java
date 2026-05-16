package hr.fer.rest_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferencijaId implements Serializable {
    private Integer korisnik;
    private Integer sport;
}