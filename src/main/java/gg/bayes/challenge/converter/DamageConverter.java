package gg.bayes.challenge.converter;

import gg.bayes.challenge.domain.Damage;
import gg.bayes.challenge.rest.model.HeroDamage;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DamageConverter {

    public static HeroDamage fromDomainToDto(@NotNull Damage damage) {
        HeroDamage heroDamage = new HeroDamage();
        BeanUtils.copyProperties(damage, heroDamage);
        return heroDamage;
    }

    public static List<HeroDamage> fromDomainToDto(@NotNull List<Damage> damage) {
        return damage.stream()
                .map(DamageConverter::fromDomainToDto)
                .collect(Collectors.toList());
    }
}
