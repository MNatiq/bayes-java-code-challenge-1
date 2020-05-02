package gg.bayes.challenge.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HeroSpells extends BaseHeroSetting {
    private String spell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id")
    private Hero hero;
}
