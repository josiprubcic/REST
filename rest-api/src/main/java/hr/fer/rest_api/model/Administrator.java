package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "administrator", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {

    @Id
    @Column(name = "id_korisnik")
    private Integer idKorisnik;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_korisnik")
    private Korisnik korisnik;
}
