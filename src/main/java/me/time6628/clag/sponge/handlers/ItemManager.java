package me.time6628.clag.sponge.handlers;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.utils.EntityHelpers;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ItemManager {

    private final CatClearLag plugin = CatClearLag.instance;

    private Set<Item> items;
    private Map<Item, SpongeExecutorService.SpongeFuture> futures;

    public ItemManager() {
        this.items = new HashSet<>();
        this.futures = new HashMap<>();
    }

    @Listener
    public void onEntitySpawn(DropItemEvent.Dispense event, @Getter("getEntities") List<Entity> entity) {
        entity.forEach(entity1 -> {
            items.add((Item) entity1);
            SpongeExecutorService.SpongeFuture future = plugin.getGame().getScheduler().createAsyncExecutor(plugin)
                    .schedule(() -> {
                        items.remove(entity1);
                    }, plugin.getCclConfig().liveTime.minLiveTime, TimeUnit.SECONDS);
            futures.put((Item) entity1, future);
        });
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onEntityDespawn(ChangeInventoryEvent.Pickup.Pre event, @Getter("getTargetEntity") Item entity) {
        futures.remove(entity).getTask().cancel();
    }

    public Set<Item> getItems() {
        return items;
    }
}
