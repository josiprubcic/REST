package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rezervacija", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_centar", "id_teren", "vrijeme_pocetka"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rezervacija {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rezervacija_seq")
    @SequenceGenerator(name = "rezervacija_seq", sequenceName = "rezervacija_id_rezervacija_seq", allocationSize = 1)
    @Column(name = "id_rezervacija")
    private Long idRezervacija;

    @ManyToOne
    @JoinColumn(name = "id_korisnik", nullable = false)
    private SportasRekreativac sportasRekreativac;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id_centar", referencedColumnName = "id_centar", nullable = false),
            @JoinColumn(name = "id_teren", referencedColumnName = "id_teren", nullable = false)
    })
    private Teren teren;

    @Column(name = "vrijeme_pocetka", nullable = false)
    private LocalDateTime vrijemePocetka;

    @Column(name = "vrijeme_zavrsetka", nullable = false)
    private LocalDateTime vrijemeZavrsetka;
}
