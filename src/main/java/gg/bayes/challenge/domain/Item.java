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
@Table(name = "items")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "hero", nullable = false)
    private String hero;

    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
