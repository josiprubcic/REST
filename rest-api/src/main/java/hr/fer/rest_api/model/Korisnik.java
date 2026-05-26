package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "korisnik", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_korisnik")
    private Integer idKorisnik;

    @Column(name = "ime")
    private String ime;

    @Column(name = "prezime")
    private String prezime;

    @Column(name = "email")
    private String email;

    @Column(name = "tip_korisnika")
    private String tipKorisnika;

    @Column(name = "lozinka")
    private String lozinka;
}
