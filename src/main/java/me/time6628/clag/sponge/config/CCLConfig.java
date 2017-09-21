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
    public List<String> entityWhiteList = new ArrayList<String>(){{add(EntityTypes.BOAT.getId());}};

    @Setting("Limits")
    public Limits limits = new Limits();

    @ConfigSerializable
    public static class Limits {

        @Setting("Max Mobs Per Chunk")
        public int mobLimitPerChunk = 20;

        @Setting("Hostile Limit")
        public int hostileLimit = 500;

        @Setting("Entity Check Interval")
        public int entityCheckInterval = 5;

        @Setting("XP Orb Limit")
        public int maxXPOrbs = 300;
    }

}