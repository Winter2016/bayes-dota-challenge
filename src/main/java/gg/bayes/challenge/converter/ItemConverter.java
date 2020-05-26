package gg.bayes.challenge.converter;

import gg.bayes.challenge.domain.Item;
import gg.bayes.challenge.rest.model.HeroItems;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemConverter {

    public static HeroItems fromDomainToDto(@NotNull Item item) {
        HeroItems heroItems = new HeroItems();
        BeanUtils.copyProperties(item, heroItems);
        return heroItems;
    }

    public static List<HeroItems> fromDomainToDto(@NotNull List<Item> Items) {
        return Items.stream()
                .map(ItemConverter::fromDomainToDto)
                .collect(Collectors.toList());
    }
}
