package gg.bayes.challenge.service;

import gg.bayes.challenge.entity.BaseHeroSetting;

public interface ParserService {
    BaseHeroSetting parse(String[] str, long matchId);
}
