package gg.bayes.challenge.repository;

import gg.bayes.challenge.entity.HeroSpells;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroSpellsRepository extends JpaRepository<HeroSpells, Long> {
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroSpells(hs.spell, count(hs.spell)) FROM Hero h, HeroSpells hs "
            + "WHERE h.id = hs.hero.id "
            + "AND h.matchId=:matchId "
            + "AND h.name=:heroName "
            + "GROUP BY hs.spell"
    )
    List<gg.bayes.challenge.rest.model.HeroSpells> findHeroSpellsByMatchIdAndHeroName(@Param("matchId") Long matchId, @Param("heroName") String heroName);
}
