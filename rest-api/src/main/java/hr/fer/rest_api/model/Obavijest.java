package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "obavijest", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Obavijest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obavijesti")
    private Integer idObavijesti;

    @Column(name = "tekst")
    private String tekst;

    @Column(name = "datum_obavijesti")
    private LocalDateTime datumObavijesti;

    @ManyToOne
    @JoinColumn(name = "id_korisnik_vlasnik")
    private VlasnikCentra vlasnikCentra;

    @ManyToOne
    @JoinColumn(name = "id_korisnik_admin")
    private Administrator administrator;
}