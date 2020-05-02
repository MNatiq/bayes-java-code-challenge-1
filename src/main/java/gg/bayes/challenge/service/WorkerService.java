package gg.bayes.challenge.service;

public interface WorkerService {
    void doRun(int finalStart, long matchId, int lineCountForPerThread, String[] lines);
}
