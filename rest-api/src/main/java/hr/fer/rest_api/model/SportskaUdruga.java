package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sportska_udruga", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportskaUdruga {

    @Id
    @Column(name = "id_korisnik")
    private Integer idKorisnik;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_korisnik")
    private Korisnik korisnik;

    @Column(name = "naziv_udruge")
    private String nazivUdruge;
}