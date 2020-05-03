package gg.bayes.challenge;

import gg.bayes.challenge.entity.Hero;
import gg.bayes.challenge.repository.HeroRepository;
import gg.bayes.challenge.rest.controller.MatchController;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
class DotaChallengeRestTests {

  @Autowired
  HeroRepository heroRepository;

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MatchController controller;

  @Autowired private TransactionTemplate transactionTemplate;

  @Test
  @Order(1)
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

  @Test
  @Order(2)
  void ingestMatchTestLog1() throws IOException, InterruptedException {

    String payload = new String(Files.readAllBytes(Paths.get("data/combatlog_1.log.txt")));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);
    String url = "http://localhost:" + port + "/api/match";
    Long matchId = restTemplate.postForObject(url, request, Long.class);
    Assertions.assertNotNull(matchId);
  }

  @Test
  @Order(3)
  void getMatchTest() {
    Long matchId = heroRepository.findByName("npc_dota_hero_pangolier").getMatchId();
    Assertions.assertNotNull(matchId);
    String url = "http://localhost:" + port + "/api/match";
    HeroKills[] kills = restTemplate.getForEntity(url + "/" + matchId, HeroKills[].class).getBody();
    Assertions.assertEquals(10, kills.length);
    // TODO: add more specific tests here
  }

  @Test
  @Order(4)
  void getItemsTest() throws InterruptedException {
    Hero hero = heroRepository.findByName("npc_dota_hero_pangolier");
    Long matchId = hero.getMatchId();
    Assertions.assertNotNull(matchId);
    String url = "http://localhost:" + port + "/api/match";
    HeroItems[] items =
        restTemplate
            .getForEntity(url + "/" + matchId + "/" + hero.getName() + "/items", HeroItems[].class)
            .getBody();
    Assertions.assertEquals(71, items.length);
    // TODO: add more specific tests here
  }

  @Test
  @Order(5)
  void getSpellsTest() {}

  @Test
  @Order(6)
  void getDamageTest() {}
}
