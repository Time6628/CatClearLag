package me.time6628.clag.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityRemover<C extends Entity> {

    private final Predicate<Entity> sourceTypePredicate;

    private Class<C> sourceType;

    @SuppressWarnings("unchecked")
    public EntityRemover() {
        Type type = getClass().getGenericSuperclass();

        while (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != EntityRemover.class) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }

        this.sourceType = (Class<C>) ((ParameterizedType) type).getActualTypeArguments()[0];
        if (this.sourceType.getClass().isAssignableFrom(Entity.class)) {
            this.sourceTypePredicate = x -> true;
        } else {
            this.sourceTypePredicate = this.sourceType::isInstance;
        }
    }

    List<C> getEntitys() {
        List<C> entities = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> w = temp.getEntities().stream().filter(entity -> sourceTypePredicate.test(entity) && !(entity instanceof Player)).collect(Collectors.toList());
            entities.addAll((Collection<? extends C>) w);
        });

        return entities;
    }
}
