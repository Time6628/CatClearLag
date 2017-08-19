package me.time6628.clag.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class EntityRemover<C extends Entity> {

    private Predicate<C> predicate;

    private final Class<C> sourceType;

    public EntityRemover(Class<C> cClass) {
        sourceType = cClass;
        if (this.sourceType.getClass().isAssignableFrom(Entity.class)) {
            this.predicate = x -> true;
        } else {
            this.predicate = this.sourceType::isInstance;
        }
        predicate = predicate.and(c -> !(c instanceof Player));
    }

    @SafeVarargs
    public EntityRemover(Class<C> cClass, Predicate<C>... extraChecks) {

        sourceType = cClass;
        if (this.sourceType.getClass().isAssignableFrom(Entity.class)) {
            this.predicate = x -> true;
        } else {
            this.predicate = this.sourceType::isInstance;
        }
        for (Predicate<C> extraCheck : extraChecks) {
            predicate = predicate.and(extraCheck);
        }
        predicate = predicate.and(c -> !(c instanceof Player));
    }

    List<C> getEntitys() {
        List<C> entities = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> w = temp.getEntities().stream().filter(entity -> predicate.test((C) entity)).collect(Collectors.toList());
            entities.addAll((Collection<? extends C>) w);
        });

        return entities;
    }

    int removeEntities() {
        final int[] i = {0};
        getEntitys().forEach(c -> {
            c.remove();
            i[0]++;
        });
        return i[0];
    }


}
