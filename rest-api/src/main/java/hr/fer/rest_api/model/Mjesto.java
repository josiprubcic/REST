package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "mjesto", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mjesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mjesto")
    private Integer idMjesto;

    @Column(name = "naziv_mjesta")
    private String nazivMjesta;

    @Column(name = "postanski_broj")
    private String postanskiBroj;
}
