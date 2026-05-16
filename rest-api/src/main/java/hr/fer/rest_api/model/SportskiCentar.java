package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "sportski_centar", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportskiCentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_centar")
    private Integer idCentar;

    @Column(name = "naziv_centra")
    private String nazivCentra;

    @Column(name = "adresa")
    private String adresa;

    @ManyToOne
    @JoinColumn(name = "id_mjesto")
    private Mjesto mjesto;

    @ManyToOne
    @JoinColumn(name = "id_vlasnik")
    private VlasnikCentra vlasnikCentra;

    @Column(name = "radno_vrijeme_tjedan_od")
    private LocalTime radnoVrijemeTjedanOd;

    @Column(name = "radno_vrijeme_tjedan_do")
    private LocalTime radnoVrijemeTjedanDo;

    @Column(name = "radno_vrijeme_vikend_od")
    private LocalTime radnoVrijemeVikendOd;

    @Column(name = "radno_vrijeme_vikend_do")
    private LocalTime radnoVrijemeVikendDo;
}