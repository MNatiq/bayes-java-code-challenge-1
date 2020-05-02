package gg.bayes.challenge.rest.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class HeroKills {
    private final String hero;
    private final Long kills;
}
