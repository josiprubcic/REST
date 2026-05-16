package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "teren", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teren {

    @EmbeddedId
    private TerenId id;

    @ManyToOne
    @MapsId("idCentar")
    @JoinColumn(name = "id_centar")
    private SportskiCentar sportskiCentar;

    @Column(name = "cijena")
    private BigDecimal cijena;

    @ManyToOne
    @JoinColumn(name = "id_sport")
    private Sport sport;
}