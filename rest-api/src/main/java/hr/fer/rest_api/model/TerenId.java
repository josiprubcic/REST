package hr.fer.rest_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerenId implements Serializable {

    @Column(name = "id_centar")
    private Integer idCentar;

    @Column(name = "id_teren")
    private String idTeren;
}