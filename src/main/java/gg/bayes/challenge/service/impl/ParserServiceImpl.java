package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.entity.BaseHeroSetting;
import gg.bayes.challenge.entity.Hero;
import gg.bayes.challenge.entity.HeroDamage;
import gg.bayes.challenge.entity.HeroItems;
import gg.bayes.challenge.entity.HeroKills;
import gg.bayes.challenge.entity.HeroSpells;
import gg.bayes.challenge.entity.OperationType;
import gg.bayes.challenge.repository.HeroRepository;
import gg.bayes.challenge.service.ParserService;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParserServiceImpl implements ParserService {

    private final HeroRepository heroRepository;
    Lock lock = new ReentrantLock();

    @Override
    @Transactional
    public BaseHeroSetting parse(String[] strArray, long matchId) {
        switch (strArray.length) {
            case 4:
                return columns4(strArray, matchId);
            case 5:
                return columns5(strArray, matchId);
            case 6:
                return columns6(strArray, matchId);
            case 8:
                return columns8(strArray, matchId);
            case 9:
                return columns9(strArray, matchId);
            case 10:
                return columns10(strArray, matchId);
            default:
                log.warn("unexpected log entry");
                return null;
        }
    }

    private BaseHeroSetting columns10(String[] columns, long matchId) {
        // [00:14:03.015] npc_dota_hero_rubick hits npc_dota_hero_ember_spirit with dota_unknown for 44
        // damage (680->636)
        if (columns[1].startsWith("npc_dota_hero_")) {
            Hero hero = getHero(matchId, columns[1]);

            // Hero event
            if (columns[2].equals("hits")) {
                // damage event, damageInstances++
                return HeroDamage.builder()
                        .totalDamage(Integer.valueOf(columns[7]))
                        .target(columns[3])
                        .hero(hero)
                        .build();
            } else if (columns[2].equals("toggles") && columns[3].equals("ability")) {
                // this cast event
                return HeroSpells.builder().spell(columns[4]).hero(hero).build();
            } else {
                log.warn("unexpected log entry. length = 10");
                return null;
            }

        } else {
            log.warn("unexpected log entry. length = 10");
            return null;
        }
    }

    private Hero getHero(long matchId, String heroName) {
        Hero hero = heroRepository.findByName(heroName);
        //Check for existance hero in db,
        // if hero doesn't exist then use lock to create new hero(using this approach we use lock only if hero is missing)
        if (Objects.isNull(hero)) {
            lock.lock();
            //we need to check existance of heroName(please see 85th and 86th lines, it is possible another thread can create this resuested hero when our thread enters to 86th line)
            Hero newHero = heroRepository.findByName(heroName);
            try {
                if (Objects.isNull(newHero)) {
                    Hero heroNew = Hero.builder().matchId(matchId).name(heroName).build();
                    newHero = heroRepository.save(heroNew);
                    heroRepository.flush();
                    return newHero;
                } else return newHero;
            } finally {
                lock.unlock();
            }
        }
        return hero;
    }

    private BaseHeroSetting columns9(String[] columns, long matchId) {
        // [00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on
        // dota_unknown
        // [00:11:17.489] npc_dota_hero_mars hits npc_dota_hero_snapfire with mars_spear for 74 damage
        if (columns[1].startsWith("npc_dota_hero_")) {
            Hero hero = getHero(matchId, columns[1]);

            // Hero event
            if (columns[2].equals("casts") && columns[3].equals("ability")) {
                // this cast event
                String spell = columns[4];

                return HeroSpells.builder().spell(spell).hero(hero).build();
            } else if (columns[2].equals("hits")) {
                // damage event, damageInstances++
                return HeroDamage.builder()
                        .totalDamage(Integer.valueOf(columns[7]))
                        .target(columns[3])
                        .hero(hero)
                        .build();
            } else if (columns[3].equals("heals")) {
                // not event we need
                return null;
            } else {
                log.warn("unexpected log entry. length = 9");
                return null;
            }

        } else if (columns[1].startsWith("npc_dota_")) {
            // not Hero event to track
            return null;
        } else {
            log.warn("unexpected log entry. length = 9");
            return null;
        }
    }

    private BaseHeroSetting columns8(String[] columns, long matchId) {
        if (columns[1].startsWith("player")) {
            // not Hero event
            return null;
        } else {
            log.warn("unexpected log entry. length = 9");
            return null;
        }
    }

    private BaseHeroSetting columns6(String[] columns, long matchId) {
        // [00:13:24.825] game state is now 10
        // [00:14:18.811] npc_dota_lycan_wolf1 is killed by npc_dota_lycan_wolf1
        // [00:37:21.614] npc_dota_goodguys_melee_rax_mid is killed by npc_dota_hero_bloodseeker

        if (columns[1].startsWith("game")) {
            // do nothing
            return null;
        } else if (columns[1].startsWith("npc_dota_hero_")) {
            Hero hero = getHero(matchId, columns[1]);

            // our Hero event
            // [00:11:17.489] npc_dota_hero_snapfire is killed by npc_dota_hero_mars
            if (columns[2].equals("is") && columns[3].equals("killed") && columns[4].equals("by")) {
                String killer = columns[5];
                // may be hero, may be not
                if (killer.startsWith("npc_dota_hero_")) {
                    // kill event, +1 to killer
                    return HeroKills.builder().hero(hero).build();
                } else {
                    // do nothing. the killer is not hero
                    return null;
                }
            } else {
                log.warn("unexpected log entry. length = 6");
                return null;
            }
        } else if (columns[1].startsWith("npc_dota_")) {
            // not our Hero, but it is possible that our Hero can "kill" in this event
            if (columns[2].equals("is") && columns[3].equals("killed") && columns[4].equals("by")) {
                String killer = columns[5];
                Hero hero = getHero(matchId, killer);
                // may be hero, may be not
                if (killer.startsWith("npc_dota_hero_")) {

                    // kill event, +1 to killer
                    return HeroKills.builder().hero(hero).build();
                } else {
                    // do nothing. the killer is not hero
                    return null;
                }
            } else {
                log.warn("unexpected log entry. length = 6");
                return null;
            }
        } else {
            log.warn("unexpected log entry. length = 6");
            return null;
        }
    }

    private HeroItems columns5(String[] columns, long matchId) {
        // [00:08:46.693] npc_dota_hero_snapfire buys item item_clarity
        if (columns[1].startsWith("npc_dota_hero_")) {
            String timestamp = columns[0];
            Hero hero = getHero(matchId, columns[1]);

            // Hero Item event
            if (columns[2].equals("buys") && columns[3].equals("item")) {
                String item = columns[4];
                // add to the list

                return HeroItems.builder()
                        .item(item)
                        .operationType(OperationType.BUY)
                        .hero(hero)
                        .timestamp(
                                LocalTime.of(
                                        Integer.parseInt(timestamp.substring(1, 3)),
                                        Integer.parseInt(timestamp.substring(4, 6)),
                                        Integer.parseInt(timestamp.substring(7, 9)))
                                        .toNanoOfDay())
                        .build();
            } else {
                log.warn("unexpected log entry. length = 4");
                return null;
            }
        } else {
            log.warn("unexpected log entry. length = 4");
            return null;
        }
    }

    private HeroItems columns4(String[] columns, long matchId) {
        // [00:13:36.322] npc_dota_hero_grimstroke uses item_tango
        if (columns[1].startsWith("npc_dota_hero_")) {
            String timestamp = columns[0];

            Hero hero = getHero(matchId, columns[1]);

            if (columns[2].equals("uses")) {
                String item = columns[3];

                return HeroItems.builder()
                        .item(item)
                        .operationType(OperationType.USE)
                        .hero(hero)
                        .timestamp(
                                LocalTime.of(
                                        Integer.parseInt(timestamp.substring(1, 3)),
                                        Integer.parseInt(timestamp.substring(4, 6)),
                                        Integer.parseInt(timestamp.substring(7, 9)))
                                        .toNanoOfDay())
                        .build();
            } else {
                log.warn("unexpected log entry. length = 4");
                return null;
            }
        } else {
            log.warn("unexpected log entry. length = 4");
            return null;
        }
    }
}

