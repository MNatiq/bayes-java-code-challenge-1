package gg.bayes.challenge.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "matchId"})})
public class Hero {

    @Id @GeneratedValue private Long id;

    private Long matchId;
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hero")
    private List<HeroDamage> heroDamages = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hero")
    private List<HeroItems> heroItems = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hero")
    private List<HeroKills> heroKills = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hero")
    private List<HeroSpells> heroSpells = new ArrayList<>();
}

