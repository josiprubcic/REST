package hr.fer.rest_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TerenId implements Serializable {

    @Column(name = "id_centar")
    private Integer idCentar;

    @Column(name = "id_teren")
    private String idTeren;
}
