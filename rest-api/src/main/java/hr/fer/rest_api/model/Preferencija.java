package hr.fer.rest_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "preferencija", schema = "public")
@IdClass(PreferencijaId.class)
@Data
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