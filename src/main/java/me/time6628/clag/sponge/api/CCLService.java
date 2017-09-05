package me.time6628.clag.sponge.api;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.function.Predicate;

public class CCLService {
    private Map<Type, Predicate> checks;

    public CCLService() {
        Predicate playerPredicate = o -> !(o instanceof Player);
        Predicate<Item> whitelistCheck = item -> !CatClearLag.instance.getCclConfig().whitelist.contains(item.getItemType().getBlock().map(blockType -> blockType.getDefaultState().getId()).orElseGet(() -> item.getItemType().getId()));
        checks.put(Type.HOSTILE, playerPredicate.and(o -> o instanceof Hostile));
        checks.put(Type.ITEM, playerPredicate.and(o -> o instanceof Item).and(whitelistCheck));
        checks.put(Type.ALL, playerPredicate.and(Entity.class::isInstance));
        checks.put(Type.LIVING, playerPredicate.and(o -> o instanceof Living));
    }

    /**
     * Add a predicate to be used when CatClearLag checks entities.
     * @param type The type entity to add the check to.
     * @param predicateToAdd A custom predicate to be used when the entities are checked.
     * @return The new predicate.
     */
    public Predicate addCheck(Type type, Predicate predicateToAdd) {
        return checks.put(type, checks.get(type).and(predicateToAdd));
    }

    /**
     * Remove a predicate to be used when CatClearLag checks entities.
     * Please note, it negates it to remove it, so provide a un-negated predicate.
     * @param type The type entity to add the check to.
     * @param predicateToRemove A custom predicate to be used when the entity is checked.
     * @return The new predicate.
     */
    public Predicate removeCheck(Type type, Predicate predicateToRemove) {
        return checks.put(type, checks.get(type).and(predicateToRemove.negate()));
    }

    public Predicate getPredicate(Type type) {
        return checks.get(type);
    }

}
