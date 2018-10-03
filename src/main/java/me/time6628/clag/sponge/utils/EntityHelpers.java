package me.time6628.clag.sponge.utils;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.EntityRemover;
import me.time6628.clag.sponge.api.Type;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;

import java.util.List;

public class EntityHelpers {

    private final static CatClearLag plugin = CatClearLag.instance;

    public static List<Hostile> getHostiles() {
        return new EntityRemover<Hostile>(plugin.getCclService().getPredicate(Type.HOSTILE)).getEntities();
    }

    public static Integer removeLiving() {
        return new EntityRemover<Living>(plugin.getCclService().getPredicate(Type.LIVING)).removeEntities();
    }

    public static List<ExperienceOrb> getXPOrbs() {
        return new EntityRemover<ExperienceOrb>(plugin.getCclService().getPredicate(Type.XP)).getEntities();
    }

    public static Integer removeXP() {
        return new EntityRemover<ExperienceOrb>(plugin.getCclService().getPredicate(Type.XP)).removeEntities();
    }

    public static Integer clearGroundItems() {
        return new EntityRemover<Item>(plugin.getCclService().getPredicate(Type.ITEM)).removeEntities();
    }

    public static Integer removeHostile() {
        return new EntityRemover<Hostile>(plugin.getCclService().getPredicate(Type.HOSTILE)).removeEntities();
    }

    public static Integer removeAll() {
        return new EntityRemover<>(plugin.getCclService().getPredicate(Type.ALL)).removeEntities();
    }
}
