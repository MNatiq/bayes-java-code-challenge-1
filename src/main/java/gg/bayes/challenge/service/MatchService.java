package gg.bayes.challenge.service;

import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;

import java.util.List;

public interface MatchService {
    Long ingestMatch(String payload);
    List<HeroKills> getHeroKillsInfo(long matchId);
    List<HeroItems> getHeroItemsInfo(long matchId, String heroName);
    List<HeroSpells> getHeroSpellsInfo(long matchId, String heroName);
    List<HeroDamage> getHeroDamageInfo(long matchId, String heroName);
}
