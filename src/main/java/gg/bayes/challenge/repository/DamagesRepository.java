package gg.bayes.challenge.repository;

import gg.bayes.challenge.domain.Damage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DamagesRepository extends JpaRepository<Damage, UUID> {
    Optional<Damage> findByMatchIdAndHeroAndTarget(Long matchId, String hero, String target);

    List<Damage> findByMatchIdAndHero(Long matchId, String hero);
}
