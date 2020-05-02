package gg.bayes.challenge.rest.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class HeroItems {
    private final String item;
    private final Long timestamp;
}
