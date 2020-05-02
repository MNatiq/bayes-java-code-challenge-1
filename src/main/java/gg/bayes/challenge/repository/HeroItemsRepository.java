package gg.bayes.challenge.repository;

import gg.bayes.challenge.entity.HeroItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroItemsRepository extends JpaRepository<HeroItems, Long> {
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroItems(hi.item, hi.timestamp)  FROM Hero h, HeroItems hi "
            + "WHERE h.id = hi.hero.id "
            + "AND h.matchId=:matchId "
            + "AND h.name=:heroName "
    )
    List<gg.bayes.challenge.rest.model.HeroItems> findHeroItemsByMatchIdAndHeroName(@Param("matchId") Long matchId, @Param("heroName") String heroName);

}
