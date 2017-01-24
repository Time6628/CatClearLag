package me.time6628.clag.sponge.handlers;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;

/**
 * Created by TimeTheCat on 1/15/2017.
 */
public class MobEventHandler {

    @Listener
    public void onMobSpawn(ConstructEntityEvent.Pre event, @Root Entity entity) {
        if (entity instanceof Living) {
            Optional<Chunk> chunk = entity.getWorld().getChunk(entity.getLocation().getChunkPosition());
            if (chunk.isPresent()) {
                if (chunk.get().getEntities().size() >= CatClearLag.instance.getMobLimitPerChunk()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
