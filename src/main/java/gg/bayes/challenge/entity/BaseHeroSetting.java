package gg.bayes.challenge.entity;

import lombok.NoArgsConstructor;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@NoArgsConstructor
public abstract class BaseHeroSetting {
    @Id @GeneratedValue private Long id;
}
