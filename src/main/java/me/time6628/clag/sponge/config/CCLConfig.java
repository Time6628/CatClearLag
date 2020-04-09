package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class CCLConfig {

    @Setting("Interval")
    public int interval = 10;

    @Setting(value = "Warnings")
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

    @ConfigSerializable
    public static class BossBar {
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

        @Setting("Min Item Live Time")
        public int minLiveTime = 20;
    }

    @ConfigSerializable
    public static class PlayerData {
        @Setting
        UUID player;
        @Setting
        long battleLimit;
    }

}