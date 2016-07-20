package me.time6628.clag.sponge;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
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
    @Inject
    private Game game;
    private Scheduler scheduler;
    private Task.Builder builder;

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
        Task warningTaskOne = builder.execute(new ItemClearingWarning(60, game))
                .async()
                .delay((long) 9, TimeUnit.MINUTES)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
        Task warningTaskTwo = builder.execute(new ItemClearingWarning(30, game))
                .async()
                .delay((long) 9.5, TimeUnit.MINUTES)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
    }
}
