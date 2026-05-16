package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "preferencija", schema = "public")
@IdClass(PreferencijaId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Preferencija {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_korisnik")
    private Korisnik korisnik;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_sport")
    private Sport sport;
}
