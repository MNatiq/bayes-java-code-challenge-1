package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.entity.*;
import gg.bayes.challenge.repository.HeroDamageRepository;
import gg.bayes.challenge.repository.HeroItemsRepository;
import gg.bayes.challenge.repository.HeroKillsRepository;
import gg.bayes.challenge.repository.HeroSpellsRepository;
import gg.bayes.challenge.service.ParserService;
import gg.bayes.challenge.service.WorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WorkerImpl implements WorkerService {

    private final HeroItemsRepository heroItemsRepository;
    private final ParserService parserService;
    private final HeroKillsRepository heroKillsRepository;
    private final HeroSpellsRepository heroSpellsRepository;
    private final HeroDamageRepository heroDamageRepository;

    @Override
    public void doRun(int finalStart, long matchId, int lineCountForPerThread, String[] lines) {
        log.info("parsing lines from: {} to: {}", finalStart, finalStart + lineCountForPerThread - 1);

        for (int i = finalStart; i < finalStart + lineCountForPerThread; ++i) {
            String line = lines[i];
            log.trace("parsing line {}", line);
            String[] columns = line.split("\\s+"); // one space or more
            if (columns.length < 1) {
                log.warn("unexpected log entry. no columns");
            }
            BaseHeroSetting baseHeroSetting = parserService.parse(columns, matchId);
            if (baseHeroSetting instanceof HeroItems)
                heroItemsRepository.save((HeroItems) baseHeroSetting);
            else if (baseHeroSetting instanceof HeroKills)
                heroKillsRepository.save((HeroKills) baseHeroSetting);
            else if (baseHeroSetting instanceof HeroSpells)
                heroSpellsRepository.save((HeroSpells) baseHeroSetting);
            else if (baseHeroSetting instanceof HeroDamage)
                heroDamageRepository.save((HeroDamage) baseHeroSetting);
        }
    }
}

