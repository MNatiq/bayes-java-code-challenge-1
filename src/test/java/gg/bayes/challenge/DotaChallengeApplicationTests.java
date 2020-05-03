package gg.bayes.challenge;

import gg.bayes.challenge.repository.HeroDamageRepository;
import gg.bayes.challenge.repository.HeroItemsRepository;
import gg.bayes.challenge.repository.HeroKillsRepository;
import gg.bayes.challenge.repository.HeroRepository;
import gg.bayes.challenge.repository.HeroSpellsRepository;
import gg.bayes.challenge.service.impl.MatchServiceImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class DotaChallengeApplicationTests {

    @Autowired ApplicationContext applicationContext;
    @Autowired MatchServiceImpl matchService;
    @Autowired HeroRepository heroRepository;
    @Autowired HeroItemsRepository heroItemsRepository;
    @Autowired HeroDamageRepository heroDamageRepository;
    @Autowired HeroKillsRepository heroKillsRepository;
    @Autowired HeroSpellsRepository heroSpellsRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

    @Test
    @Disabled
    void matchServiceCombatLog2Test() {
        // TODO: add more specific test here
    }

    @Test
    void matchServiceCombatLog1Test() throws IOException {
        String payload = new String(Files.readAllBytes(Paths.get("data/combatlog_1.log.txt")));
        Long matchId = matchService.ingestMatch(payload);

        Assertions.assertEquals(33, heroRepository.count());
        Assertions.assertEquals(930, heroItemsRepository.count());
        Assertions.assertEquals(2558, heroDamageRepository.count());
        Assertions.assertEquals(460, heroKillsRepository.count());
        Assertions.assertEquals(850, heroSpellsRepository.count());

        Assertions.assertEquals(
                88,
                heroItemsRepository
                        .findHeroItemsByMatchIdAndHeroName(matchId, "npc_dota_hero_dragon_knight")
                        .size());

        // TODO: add more specific test here

    }
}
