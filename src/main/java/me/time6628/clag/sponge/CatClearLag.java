package me.time6628.clag.sponge;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import me.time6628.clag.sponge.runnables.ItemClearer;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.1", description = "DIE LAG, DIE!")
public class CatClearLag {
    @Inject Game game;
    Scheduler scheduler;
    Task.Builder builder;
    @Subscribe
    public void onPreInit(GamePreInitializationEvent event) {
        scheduler = game.getScheduler();
        builder = scheduler.createTaskBuilder();
        Task task = builder.execute(new ItemClearer(game))
                .async()
                .delay(10, TimeUnit.MINUTES)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);

    }
}
