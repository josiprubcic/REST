package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "dogadaj", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dogadaj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dogadaj")
    private Integer idDogadaj;

    @Column(name = "naziv_dogadaja")
    private String nazivDogadaja;

    @Column(name = "datum_odrzavanja")
    private LocalDateTime datumOdrzavanja;

    @ManyToOne
    @JoinColumn(name = "id_korisnik_udruga")
    private SportskaUdruga sportskaUdruga;

    @ManyToOne
    @JoinColumn(name = "id_korisnik_admin")
    private Administrator administrator;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id_centar", referencedColumnName = "id_centar"),
            @JoinColumn(name = "id_teren", referencedColumnName = "id_teren")
    })
    private Teren teren;
}