package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemTypes;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CCLConfig {

    @Setting("Interval")
    public final int interval = 10;

    @Setting(value = "Warnings")
    public final List<Integer> warnings = new ArrayList<Integer>() {{
        add(540);
        add(570);
    }};

    @Setting("Whitelist")
    public final List<String> whitelist = new ArrayList<String>() {{
        add(ItemTypes.DIAMOND.getId());
        add(BlockTypes.DIAMOND_BLOCK.getId());
        add(BlockTypes.BEACON.getDefaultState().getId());
    }};

    @Setting("Entity Whitelist")
    public final List<String> entityWhiteList = new ArrayList<String>(){{add(EntityTypes.BOAT.getId());}};

    @Setting("Limits")
    public final
    Limits limits = new Limits();

    @ConfigSerializable
    public static class Limits {

        @Setting("Max Mobs Per Chunk")
        public final int mobLimitPerChunk = 20;

        @Setting("Hostile Limit")
        public final int hostileLimit = 500;

        @Setting("Entity Check Interval")
        public final int entityCheckInterval = 5;

        @Setting("XP Orb Limit")
        public final int maxXPOrbs = 300;
    }

}