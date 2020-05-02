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
public class HeroDamage extends BaseHeroSetting {

    private String target;
    private Integer totalDamage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id")
    private Hero hero;
}
