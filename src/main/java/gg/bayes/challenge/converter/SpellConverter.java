package gg.bayes.challenge.converter;

import gg.bayes.challenge.domain.Spell;
import gg.bayes.challenge.rest.model.HeroSpells;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SpellConverter {

    public static HeroSpells fromDomainToDto(@NotNull Spell spell) {
        HeroSpells heroSpell = new HeroSpells();
        BeanUtils.copyProperties(spell, heroSpell);
        return heroSpell;
    }

    public static List<HeroSpells> fromDomainToDto(@NotNull List<Spell> spell) {
        return spell.stream()
                .map(SpellConverter::fromDomainToDto)
                .collect(Collectors.toList());
    }
}
