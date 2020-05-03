package gg.bayes.challenge;

import gg.bayes.challenge.entity.BaseHeroSetting;
import gg.bayes.challenge.entity.Hero;
import gg.bayes.challenge.entity.HeroKills;
import gg.bayes.challenge.entity.HeroSpells;
import gg.bayes.challenge.repository.HeroRepository;
import gg.bayes.challenge.service.ParserService;
import gg.bayes.challenge.service.impl.ParserServiceImpl;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

// TODO: add other event types test
public class DotaChallengeUnitTests {

  private static ParserService parserService;
  private static HeroRepository heroRepository;

  private static Stream<TestData<HeroSpells>> getSpellTestData() {
    return Stream.of(
        new TestData<>(
            "[00:34:10.295] npc_dota_hero_rubick casts ability rubick_spell_steal (lvl 2) on "
                + "npc_dota_hero_abyssal_underlord",
            HeroSpells.builder()
                .hero(Hero.builder().matchId(10L).name("npc_dota_hero_rubick").build())
                .spell("rubick_spell_steal")
                .build())
        // and so on for all test types
        );
  }

  private static Stream<TestData<HeroKills>> getKillsTestData() {
    return Stream.of(
        new TestData<>(
            "[00:33:36.595] npc_dota_hero_bane is killed by npc_dota_hero_pangolier",
            HeroKills.builder()
                .hero(Hero.builder().matchId(11L).name("npc_dota_hero_bane").build())
                .build())
        // and so on for all test types
        );
  }

  @BeforeAll
  static void beforeAll() {
    heroRepository = Mockito.mock(HeroRepository.class);
    parserService = new ParserServiceImpl(heroRepository);
  }

  @ParameterizedTest
  @MethodSource("getKillsTestData")
  void heroKillsTest(TestData<HeroKills> testData) {

    Mockito.when(heroRepository.findByName(testData.expectedValue.getHero().getName()))
        .thenReturn(null);
    Mockito.when(heroRepository.save(Mockito.any(Hero.class)))
        .thenReturn(testData.expectedValue.getHero());

    BaseHeroSetting baseHeroSetting =
        parserService.parse(testData.getColumns(), testData.expectedValue.getHero().getMatchId());

    Assertions.assertNotNull(baseHeroSetting);
    Assertions.assertTrue(baseHeroSetting instanceof HeroKills);

    HeroKills actualHeroKills = (HeroKills) baseHeroSetting;

    Assertions.assertEquals(testData.expectedValue, actualHeroKills);
  }

  @ParameterizedTest
  @MethodSource("getSpellTestData")
  void heroSpellsTest(TestData<HeroSpells> testData) {

    Mockito.when(heroRepository.findByName(testData.expectedValue.getHero().getName()))
        .thenReturn(null);
    Mockito.when(heroRepository.save(Mockito.any(Hero.class)))
        .thenReturn(testData.expectedValue.getHero());

    BaseHeroSetting baseHeroSetting =
        parserService.parse(testData.getColumns(), testData.expectedValue.getHero().getMatchId());

    Assertions.assertNotNull(baseHeroSetting);
    Assertions.assertTrue(baseHeroSetting instanceof HeroSpells);

    HeroSpells actualHeroSpells = (HeroSpells) baseHeroSetting;

    Assertions.assertEquals(testData.expectedValue, actualHeroSpells);
  }

  @AllArgsConstructor
  private static class TestData<T> {

    String line;
    T expectedValue;

    public String[] getColumns() {
      return line.split("\\s+");
    }
  }
}
