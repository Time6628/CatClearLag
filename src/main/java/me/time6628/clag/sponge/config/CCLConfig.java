package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemTypes;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CCLConfig {

    @Setting(value = "Interval", comment = "Interval in minutes to run entity clear.")
    public int interval = 10;

    @Setting(value = "Item Despawn Rate", comment = "Set this to the same value as in the Sponge config, otherwise it will not work correctly")
    public int itemDespawnRate = 6000;

    @Setting(value = "Warnings", comment = "interval * 60 - seconds you want to warn at")
    public List<Integer> warnings = new ArrayList<Integer>() {{
        add(540);
        add(570);
    }};

    @Setting("Whitelist")
    public List<String> whitelist = new ArrayList<String>() {{
        add(ItemTypes.DIAMOND.getId());
        add(BlockTypes.DIAMOND_BLOCK.getId());
        add(BlockTypes.BEACON.getDefaultState().getId());
    }};

    @Setting("Entity Whitelist")
    public List<String> entityWhiteList = new ArrayList<String>() {{
        add(EntityTypes.BOAT.getId());
    }};

    @Setting("Limits")
    public Limits limits = new Limits();

    @Setting("Live Time")
    public LiveTime liveTime = new LiveTime();

    @Setting("Boss Bar")
    public BossBar bossBar = new BossBar();

    @Setting("Sounds")
    public Sounds sounds = new Sounds();

    @ConfigSerializable
    public static class BossBar {

        @Setting
        public boolean enabled = true;

        @Setting(value = "Hide Boss Bar After", comment = "Hide the boss bar after this amount of time.")
        public int hideBoss = 15;

        @Setting(value = "Boss Bar Color", comment = "BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW")
        public BossBarColor bossBarColor = BossBarColors.RED;
    }

    @ConfigSerializable
    public static class Limits {

        @Setting("Per Chunk Limit Enabled")
        public boolean perChunkLimitEnabled = false;

        @Setting("Max Mobs Per Chunk")
        public int mobLimitPerChunk = 20;

        @Setting("Hostile Limit")
        public int hostileLimit = 500;

        @Setting("Entity Check Interval")
        public int entityCheckInterval = 5;

        @Setting("XP Orb Limit")
        public int maxXPOrbs = 300;
    }

    @ConfigSerializable
    public static class LiveTime {
        @Setting
        public boolean enabled = false;

        @Setting(value = "Min Item Live Time", comment = "Minimum amount of time an item should live before being able to be cleared")
        public int minLiveTime = 20;
    }

    @ConfigSerializable
    public static class Sounds {
        @Setting
        public boolean enabled = true;

        @Setting(value = "Warning sound", comment = "Sound sent to all players for warnings")
        public SoundType warningSound = SoundTypes.ITEM_BOTTLE_FILL;

        @Setting(value = "Cleared sound", comment = "Sound sent to all players for when items are cleared")
        public SoundType clearedSound = SoundTypes.ENTITY_SPLASH_POTION_BREAK;

        @Setting(value = "Sound Category")
        public SoundCategory soundCategory = SoundCategories.VOICE;
    }

}