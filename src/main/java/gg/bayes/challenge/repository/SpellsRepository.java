package gg.bayes.challenge.repository;

import gg.bayes.challenge.domain.Spell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpellsRepository extends JpaRepository<Spell, UUID> {
    Optional<Spell> findByMatchIdAndHeroAndSpell(Long matchId, String hero, String spell);

    List<Spell> findByMatchIdAndHero(Long matchId, String hero);
}
