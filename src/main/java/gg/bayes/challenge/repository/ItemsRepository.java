package gg.bayes.challenge.repository;

import gg.bayes.challenge.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemsRepository extends JpaRepository<Item, UUID> {
    List<Item> findByMatchIdAndHero(Long matchId, String heroName);
}
