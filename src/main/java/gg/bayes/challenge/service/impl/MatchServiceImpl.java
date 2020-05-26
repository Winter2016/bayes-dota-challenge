package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.converter.DamageConverter;
import gg.bayes.challenge.converter.ItemConverter;
import gg.bayes.challenge.converter.KillConverter;
import gg.bayes.challenge.converter.SpellConverter;
import gg.bayes.challenge.domain.Damage;
import gg.bayes.challenge.domain.Item;
import gg.bayes.challenge.domain.Kill;
import gg.bayes.challenge.domain.Spell;
import gg.bayes.challenge.repository.DamagesRepository;
import gg.bayes.challenge.repository.ItemsRepository;
import gg.bayes.challenge.repository.KillsRepository;
import gg.bayes.challenge.repository.SpellsRepository;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final DamagesRepository damagesRepository;
    private final ItemsRepository itemsRepository;
    private final KillsRepository killsRepository;
    private final SpellsRepository spellsRepository;

    private static final String DAMAGE_REGEX = "^[\\[][0-9]{2}[:][0-9]{2}[:][0-9]{2}[.][0-9]{3}[\\]]\\snpc_dota_hero_(?<hero>[a-zA-z0-9]+)\\shits\\snpc_dota_hero_(?<target>[a-zA-z0-9]+)\\swith\\s[a-zA-z0-9]+\\sfor\\s(?<damage>[0-9]{1,5})\\sdamage\\s[\\(][0-9]{1,5}->[0-9]{1,5}[/)]$";
    private static final String ITEM_REGEX = "^[\\[](?<time>[0-9]{2}[:][0-9]{2}[:][0-9]{2}[.][0-9]{3})[\\]]\\snpc_dota_hero_(?<hero>[a-zA-z0-9]+)\\sbuys\\sitem\\sitem_(?<item>[a-zA-z0-9]+)$";
    private static final String KILL_REGEX = "^[\\[][0-9]{2}[:][0-9]{2}[:][0-9]{2}[.][0-9]{3}[\\]]\\snpc_dota_[a-zA-z0-9]+\\sis\\skilled\\sby\\snpc_dota_hero_(?<hero>[a-zA-z0-9]+)$";
    private static final String SPELL_REGEX = "^[\\[][0-9]{2}[:][0-9]{2}[:][0-9]{2}[.][0-9]{3}[\\]]\\snpc_dota_hero_(?<hero>[a-zA-z0-9]+)\\scasts\\sability\\s(?<spell>[a-zA-z0-9]+)\\s[\\(]lvl\\s[0-9]{1,3}[\\)]\\son\\s[a-zA-z0-9]+$";
    private static final Pattern DAMAGE_PATTERN = Pattern.compile(DAMAGE_REGEX);
    private static final Pattern ITEM_PATTERN = Pattern.compile(ITEM_REGEX);
    private static final Pattern KILL_PATTERN = Pattern.compile(KILL_REGEX);
    private static final Pattern SPELL_PATTERN = Pattern.compile(SPELL_REGEX);

    @Override
    public Long ingestMatch(String payload) {
        log.info("Going to parse game logs for a new match");
        Long matchId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        String[] logs = payload.split("\n");
        for (String log : logs) {
            if (log.matches(DAMAGE_REGEX)) {
                saveDamage(log, matchId);
            } else if (log.matches(ITEM_REGEX)) {
                saveItem(log, matchId);
            } else if (log.matches(KILL_REGEX)) {
                saveKill(log, matchId);
            } else if (log.matches(SPELL_REGEX)) {
                saveSpell(log, matchId);
            }
        }
        log.debug("Saved logs for a new match with id {}", matchId);
        return matchId;
    }

    @Override
    public List<HeroKills> getKills(Long matchId) {
        List<Kill> kills = killsRepository.findByMatchId(matchId);
        return KillConverter.fromDomainToDto(kills);
    }

    @Override
    public List<HeroItems> getItems(Long matchId, String heroName) {
        List<Item> items = itemsRepository.findByMatchIdAndHero(matchId, heroName);
        return ItemConverter.fromDomainToDto(items);
    }

    @Override
    public List<HeroSpells> getSpells(Long matchId, String heroName) {
        List<Spell> spells = spellsRepository.findByMatchIdAndHero(matchId, heroName);
        return SpellConverter.fromDomainToDto(spells);
    }

    @Override
    public List<HeroDamage> getDamages(Long matchId, String heroName) {
        List<Damage> damages = damagesRepository.findByMatchIdAndHero(matchId, heroName);
        return DamageConverter.fromDomainToDto(damages);
    }

    private void saveSpell(String log, Long matchId) {
        Matcher matcher = SPELL_PATTERN.matcher(log);

        if (matcher.find()) {
            String hero = matcher.group("hero");
            String spell = matcher.group("spell");
            Spell heroSpell = spellsRepository.findByMatchIdAndHeroAndSpell(matchId, hero, spell)
                    .map(this::incrementSpellCasts)
                    .orElseGet(() -> createSpell(matchId, hero, spell));

            spellsRepository.save(heroSpell);
        }
    }

    private Spell incrementSpellCasts(Spell s) {
        s.setCasts(s.getCasts() + 1);
        return s;
    }

    private Spell createSpell(Long matchId, String hero, String spell) {
        return Spell.builder()
                .matchId(matchId)
                .hero(hero)
                .spell(spell)
                .casts(1)
                .build();
    }

    private void saveKill(String log, Long matchId) {
        Matcher matcher = KILL_PATTERN.matcher(log);

        if (matcher.find()) {
            String hero = matcher.group("hero");
            Kill kill = killsRepository.findByMatchIdAndHero(matchId, hero)
                    .map(this::incrementKills)
                    .orElseGet(() -> createKill(matchId, hero));

            killsRepository.save(kill);
        }
    }

    private Kill incrementKills(Kill k) {
        k.setKills(k.getKills() + 1);
        return k;
    }

    private Kill createKill(Long matchId, String hero) {
        return Kill.builder()
                .matchId(matchId)
                .hero(hero)
                .kills(1)
                .build();
    }

    private void saveItem(String log, Long matchId) {
        Matcher matcher = ITEM_PATTERN.matcher(log);

        if (matcher.find()) {
            Long time = LocalTime.parse(matcher.group("time")).toNanoOfDay() / 1000000;
            String hero = matcher.group("hero");
            String item = matcher.group("item");
            Item heroItem = createItem(matchId, time, hero, item);
            itemsRepository.save(heroItem);
        }
    }

    private Item createItem(Long matchId, Long time, String hero, String item) {
        return Item.builder()
                .matchId(matchId)
                .hero(hero)
                .item(item)
                .timestamp(time)
                .build();
    }

    private void saveDamage(String log, Long matchId) {
        Matcher matcher = DAMAGE_PATTERN.matcher(log);

        if (matcher.find()) {
            String hero = matcher.group("hero");
            String target = matcher.group("target");
            Integer damage = Integer.valueOf(matcher.group("damage"));
            Damage heroDamage = damagesRepository.findByMatchIdAndHeroAndTarget(matchId, hero, target)
                    .map(d -> updateDamage(damage, d))
                    .orElseGet(() -> createDamage(matchId, hero, target, damage));

            damagesRepository.save(heroDamage);
        }
    }

    private Damage updateDamage(Integer damage, Damage d) {
        d.setDamageInstances(d.getDamageInstances() + 1);
        d.setTotalDamage(d.getTotalDamage() + damage);
        return d;
    }

    private Damage createDamage(Long matchId, String hero, String target, Integer damage) {
        return Damage.builder()
                .matchId(matchId)
                .hero(hero)
                .target(target)
                .damageInstances(1)
                .totalDamage(damage)
                .build();
    }
}
