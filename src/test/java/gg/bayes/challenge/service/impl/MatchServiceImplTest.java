package gg.bayes.challenge.service.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private DamagesRepository damagesRepository;

    @Mock
    private ItemsRepository itemsRepository;

    @Mock
    private KillsRepository killsRepository;

    @Mock
    private SpellsRepository spellsRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Captor
    private ArgumentCaptor<Damage> damageArgumentCaptor;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    private ArgumentCaptor<Kill> killArgumentCaptor;

    @Captor
    private ArgumentCaptor<Spell> spellArgumentCaptor;

    private static final Long MATCH_ID = 1L;
    private static final String HERO = "rubick";
    private static final String TARGET = "ember_spirit";
    private static final String SPELL = "rubick_spell_steal";
    private static final String ITEM = "blade_of_alacrity";

    @Test
    void ingestMatchForSingleDamage() {
        Damage existingDamage = Damage.builder()
                .hero(HERO)
                .target(TARGET)
                .damageInstances(5)
                .totalDamage(29)
                .build();
        when(damagesRepository.findByMatchIdAndHeroAndTarget(anyLong(), eq(HERO), eq(TARGET)))
                .thenReturn(Optional.of(existingDamage));

        matchService.ingestMatch("[00:14:03.015] npc_dota_hero_rubick hits npc_dota_hero_ember_spirit with dota_unknown for 44 damage (680->636)");

        verify(damagesRepository).save(damageArgumentCaptor.capture());
        Damage damage = damageArgumentCaptor.getValue();
        assertEquals(HERO, damage.getHero());
        assertEquals(TARGET, damage.getTarget());
        assertEquals(6, damage.getDamageInstances());
        assertEquals(73, damage.getTotalDamage());
    }

    @Test
    void ingestMatchForSingleItem() {
        matchService.ingestMatch("[00:00:10.927] npc_dota_hero_rubick buys item item_blade_of_alacrity");

        verify(itemsRepository).save(itemArgumentCaptor.capture());
        Item item = itemArgumentCaptor.getValue();
        assertEquals(HERO, item.getHero());
        assertEquals(ITEM, item.getItem());
        assertEquals(10927L, item.getTimestamp());
    }

    @Test
    void ingestMatchForSingleKill() {
        Kill existingKill = Kill.builder()
                .hero(HERO)
                .kills(3)
                .build();
        when(killsRepository.findByMatchIdAndHero(anyLong(), eq(HERO)))
                .thenReturn(Optional.of(existingKill));

        matchService.ingestMatch("[00:29:24.157] npc_dota_hero_mars is killed by npc_dota_hero_rubick");

        verify(killsRepository).save(killArgumentCaptor.capture());
        Kill kill = killArgumentCaptor.getValue();
        assertEquals(HERO, kill.getHero());
        assertEquals(4, kill.getKills());
    }

    @Test
    void ingestMatchForSingleSpell() {
        Spell existingSpell = Spell.builder()
                .hero(HERO)
                .spell(SPELL)
                .casts(8)
                .build();
        when(spellsRepository.findByMatchIdAndHeroAndSpell(anyLong(), eq(HERO), eq(SPELL)))
                .thenReturn(Optional.of(existingSpell));

        matchService.ingestMatch("[00:32:50.240] npc_dota_hero_rubick casts ability rubick_spell_steal (lvl 2) on npc_dota_hero_snapfire");

        verify(spellsRepository).save(spellArgumentCaptor.capture());
        Spell spell = spellArgumentCaptor.getValue();
        assertEquals(HERO, spell.getHero());
        assertEquals(SPELL, spell.getSpell());
        assertEquals(9, spell.getCasts());
    }

    @Test
    void getKills() {
        Kill existingKill = Kill.builder()
                .hero(HERO)
                .kills(3)
                .build();
        when(killsRepository.findByMatchId(eq(MATCH_ID)))
                .thenReturn(Collections.singletonList(existingKill));

        List<HeroKills> kills = matchService.getKills(MATCH_ID);

        HeroKills expectedKills = new HeroKills();
        expectedKills.setHero(HERO);
        expectedKills.setKills(3);
        assertEquals(Collections.singletonList(expectedKills), kills);
    }

    @Test
    void getItems() {
        Item existingItem = Item.builder()
                .matchId(MATCH_ID)
                .hero(HERO)
                .item(ITEM)
                .timestamp(10927L)
                .build();
        when(itemsRepository.findByMatchIdAndHero(eq(MATCH_ID), eq(HERO)))
                .thenReturn(Collections.singletonList(existingItem));

        List<HeroItems> items = matchService.getItems(MATCH_ID, HERO);

        HeroItems expectedItems = new HeroItems();
        expectedItems.setItem(ITEM);
        expectedItems.setTimestamp(10927L);
        assertEquals(Collections.singletonList(expectedItems), items);
    }

    @Test
    void getSpells() {
        Spell existingSpell = Spell.builder()
                .matchId(MATCH_ID)
                .hero(HERO)
                .spell(SPELL)
                .casts(7)
                .build();
        when(spellsRepository.findByMatchIdAndHero(eq(MATCH_ID), eq(HERO)))
                .thenReturn(Collections.singletonList(existingSpell));

        List<HeroSpells> spells = matchService.getSpells(MATCH_ID, HERO);

        HeroSpells expectedSpells = new HeroSpells();
        expectedSpells.setSpell(SPELL);
        expectedSpells.setCasts(7);
        assertEquals(Collections.singletonList(expectedSpells), spells);
    }

    @Test
    void getDamages() {
        Damage existingDamage = Damage.builder()
                .matchId(MATCH_ID)
                .hero(HERO)
                .target(TARGET)
                .damageInstances(10)
                .totalDamage(55)
                .build();
        when(damagesRepository.findByMatchIdAndHero(eq(MATCH_ID), eq(HERO)))
                .thenReturn(Collections.singletonList(existingDamage));

        List<HeroDamage> damages = matchService.getDamages(MATCH_ID, HERO);

        HeroDamage expectedDamages = new HeroDamage();
        expectedDamages.setTarget(TARGET);
        expectedDamages.setDamageInstances(10);
        expectedDamages.setTotalDamage(55);
        assertEquals(Collections.singletonList(expectedDamages), damages);
    }
}
