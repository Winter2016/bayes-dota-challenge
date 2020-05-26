package gg.bayes.challenge.converter;

import gg.bayes.challenge.domain.Kill;
import gg.bayes.challenge.rest.model.HeroKills;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class KillConverter {

    public static HeroKills fromDomainToDto(@NotNull Kill kill) {
        HeroKills heroKills = new HeroKills();
        BeanUtils.copyProperties(kill, heroKills);
        return heroKills;
    }

    public static List<HeroKills> fromDomainToDto(@NotNull List<Kill> kills) {
        return kills.stream()
                .map(KillConverter::fromDomainToDto)
                .collect(Collectors.toList());
    }
}
