package me.time6628.clag.sponge.api;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.player.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public class CCLService {

    private final Map<Type, Predicate<Entity>> checks;

    public CCLService() {
        checks = new EnumMap<>(Type.class);
        Predicate<Entity> whitelistCheck = item -> !CatClearLag.instance.getCclConfig().whitelist.contains(((Item) item).getItemType().getId());
        Predicate<Entity> entityWhitelist = entity -> !CatClearLag.instance.getCclConfig().entityWhiteList.contains(entity.getType().getId());
        checks.put(Type.HOSTILE, notAPlayer().and(o -> o instanceof Hostile).and(entityWhitelist));
        checks.put(Type.ITEM, notAPlayer().and(o -> o instanceof Item).and(whitelistCheck));
        checks.put(Type.ALL, notAPlayer().and(Entity.class::isInstance).and(entityWhitelist));
        checks.put(Type.LIVING, notAPlayer().and(o -> o instanceof Living).and(entityWhitelist));
        checks.put(Type.XP, notAPlayer().and(o -> o instanceof ExperienceOrb));
        checks.put(Type.ANIMAL, notAPlayer().and(o -> o instanceof Animal).and(entityWhitelist));
        checks.put(Type.NAMED, notAPlayer().and(entity -> !entity.get(DisplayNameData.class).isPresent()));
        checks.put(Type.ENTITY, notAPlayer());
    }

    private Predicate<Entity> notAPlayer() {
        return entity -> !(entity instanceof Player);
    }

    /**
     * Add a predicate to be used when CatClearLag checks entities.
     *
     * @param type           The type entity to add the check to.
     * @param predicateToAdd A custom predicate to be used when the entities are checked.
     * @return The new predicate.
     */
    public Predicate<Entity> addCheck(Type type, Predicate<Entity> predicateToAdd) {
        return checks.put(type, checks.get(type).and(predicateToAdd));
    }

    /**
     * Remove a predicate to be used when CatClearLag checks entities.
     * Please note, it negates it to remove it, so provide a un-negated predicate.
     *
     * @param type              The type entity to add the check to.
     * @param predicateToRemove A custom predicate to be used when the entity is checked.
     * @return The new predicate.
     */
    public Predicate<Entity> removeCheck(Type type, Predicate<Entity> predicateToRemove) {
        return checks.put(type, checks.get(type).and(predicateToRemove.negate()));
    }

    public Predicate<Entity> getPredicate(Type type) {
        return checks.get(type);
    }

}
