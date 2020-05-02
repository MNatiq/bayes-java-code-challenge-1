package gg.bayes.challenge.repository;

import gg.bayes.challenge.entity.HeroDamage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroDamageRepository extends JpaRepository<HeroDamage, Long> {
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroDamage(hd.target, sum(hd.totalDamage), count(hd.target)) FROM Hero h, HeroDamage hd "
            + "WHERE h.id = hd.hero.id "
            + "AND h.matchId=:matchId "
            + "AND h.name=:heroName "
            + "GROUP BY hd.target, hd.totalDamage"
    )
    List<gg.bayes.challenge.rest.model.HeroDamage> findHeroItemsByMatchIdAndHeroName(@Param("matchId") Long matchId, @Param("heroName") String heroName);

}
