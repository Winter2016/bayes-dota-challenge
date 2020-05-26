package gg.bayes.challenge.repository;

import gg.bayes.challenge.domain.Kill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KillsRepository extends JpaRepository<Kill, UUID> {
    Optional<Kill> findByMatchIdAndHero(Long matchId, String hero);

    List<Kill> findByMatchId(Long matchId);
}
