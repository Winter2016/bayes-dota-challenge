package gg.bayes.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "spells")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Spell {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "hero", nullable = false)
    private String hero;

    @Column(name = "spell", nullable = false)
    private String spell;

    @Column(name = "casts", nullable = false)
    private Integer casts;
}
