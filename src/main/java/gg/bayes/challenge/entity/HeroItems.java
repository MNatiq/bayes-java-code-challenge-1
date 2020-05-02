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
public class HeroItems extends BaseHeroSetting {

    private String item;
    private Long timestamp;
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id")
    private Hero hero;
}
