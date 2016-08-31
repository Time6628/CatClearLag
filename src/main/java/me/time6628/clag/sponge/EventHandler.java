package me.time6628.clag.sponge;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;

/**
 * Created by TimeTheCat on 8/31/2016.
 */
public class EventHandler {

    @Listener
    public void onMobSpawn(ConstructEntityEvent.Post event) {
        Optional<Chunk> chunk = event.getTargetEntity().getWorld().getChunk(event.getTargetEntity().getLocation().getChunkPosition());
        Chunk entityChunk = null;
        if (chunk.isPresent()) {
            entityChunk = chunk.get();
            if (entityChunk.getEntities().size() >= 20) {
                event.getTargetEntity().remove();
            }
        }

    }
}
