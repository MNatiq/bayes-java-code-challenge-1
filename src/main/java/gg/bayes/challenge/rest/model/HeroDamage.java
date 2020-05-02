package gg.bayes.challenge.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class HeroDamage {
    private final String target;

    @JsonProperty("damage_instances")
    private final Long damageInstances;

    @JsonProperty("total_damage")
    private final Long totalDamage;
}
