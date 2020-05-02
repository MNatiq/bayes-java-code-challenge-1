package gg.bayes.challenge.repository;

import gg.bayes.challenge.entity.HeroKills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroKillsRepository extends JpaRepository<HeroKills, Long> {
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroKills(h.name, count(hk.id)) FROM Hero h, HeroKills hk "
            + "WHERE h.id = hk.hero.id "
            + "AND h.matchId=:matchId "
            + "GROUP BY h.name"
    )
    List<gg.bayes.challenge.rest.model.HeroKills> findHeroKillsByMatchId(@Param("matchId") Long matchId);

}
