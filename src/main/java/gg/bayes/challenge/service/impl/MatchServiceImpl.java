package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.repository.HeroDamageRepository;
import gg.bayes.challenge.repository.HeroItemsRepository;
import gg.bayes.challenge.repository.HeroKillsRepository;
import gg.bayes.challenge.repository.HeroSpellsRepository;
import gg.bayes.challenge.service.MatchService;
import gg.bayes.challenge.service.WorkerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    private static final int PARALLEL_LEVEL = 2;
    private final HeroItemsRepository heroItemsRepository;
    private final HeroDamageRepository heroDamageRepository;
    private final HeroKillsRepository heroKillsRepository;
    private final HeroSpellsRepository heroSpellsRepository;
    private final WorkerService workerService;

    @Override
    public Long ingestMatch(String payload) {
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(PARALLEL_LEVEL);
        try {
            String[] lines =
                    payload.split("\\R"); // or even \\R+, to avoid any end-of-line characters that were not
            // covered by \\R alone

            List<Future> futures = new ArrayList<>();
            long matchId = new Random().nextLong();
            int parallelThread = PARALLEL_LEVEL;
            int lineCountForPerThread = lines.length / parallelThread;
            int lineCountForPerThreadMod = lines.length % parallelThread;
            int start = 0;
            while (parallelThread-- > 0) {
                int finalStart = start;
                int finalLineCountForPerThread =
                        (parallelThread == 0
                                ? lineCountForPerThread + lineCountForPerThreadMod
                                : lineCountForPerThread);
                Future<?> future =
                        executor.submit(
                                () -> workerService.doRun(finalStart, matchId, finalLineCountForPerThread, lines));
                futures.add(future);
                start += lineCountForPerThread;
            }
            while (!futures.stream().allMatch(Future::isDone)) {
                log.info("Tasks are not yet complete....sleeping");
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    log.info("Occurred InterruptedException.");
                }
            }
            return matchId;
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public List<gg.bayes.challenge.rest.model.HeroKills> getHeroKillsInfo(long matchId) {
        return heroKillsRepository.findHeroKillsByMatchId(matchId);
    }

    @Override
    public List<gg.bayes.challenge.rest.model.HeroItems> getHeroItemsInfo(
            long matchId, String heroName) {
        return heroItemsRepository.findHeroItemsByMatchIdAndHeroName(matchId, heroName);
    }

    @Override
    public List<gg.bayes.challenge.rest.model.HeroSpells> getHeroSpellsInfo(
            long matchId, String heroName) {
        return heroSpellsRepository.findHeroSpellsByMatchIdAndHeroName(matchId, heroName);
    }

    @Override
    public List<gg.bayes.challenge.rest.model.HeroDamage> getHeroDamageInfo(
            long matchId, String heroName) {
        return heroDamageRepository.findHeroItemsByMatchIdAndHeroName(matchId, heroName);
    }
}