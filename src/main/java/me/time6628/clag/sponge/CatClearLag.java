package me.time6628.clag.sponge;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.time6628.clag.sponge.commands.*;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializerFactory;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.5.1", description = "DIE LAG, DIE!")
public class CatClearLag {

    @Inject
    static
    org.slf4j.Logger logger;

    //config stuff
    private ConfigurationNode cfg;

    @Inject
    PluginContainer pluginContainer;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultCfg;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> cfgMgr;

    @Inject
    public Game game;

    private Scheduler scheduler = game.getScheduler();

    public Text prefix;

    private int interval = 0;
    private List<Integer> warning;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        logger.info("Making config...");
        try {
            if (!defaultCfg.exists()) {
                defaultCfg.createNewFile();

                this.cfg = getCfgMgr().load();

                this.cfg.getNode("Version").setValue(0.1);

                this.cfg.getNode("interval").setValue(10);
                this.cfg.getNode("warnings").setValue(Arrays.asList(new int[]{540, 570}));
                this.cfg.getNode("prefix").setValue("&5[ClearLag]");
                getLogger().info("Config created.");
                getCfgMgr().save(cfg);
            }

            this.cfg = getCfgMgr().load();

            this.prefix = TextSerializers.LEGACY_FORMATTING_CODE.deserialize(cfg.getNode("prefix").getString());
            this.interval = cfg.getNode("interval").getInt();
            this.warning = cfg.getNode("warnings").getList(o -> (Integer) o);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        logger.info("Starting plugin...");
        registerCommands();
        //registerEvents();
        Task.Builder builder = scheduler.createTaskBuilder();
        Task task = builder.execute(new ItemClearer(this))
                .async()
                .delay(10, TimeUnit.MINUTES)
                .interval(interval, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);
        warning.forEach((d) ->
                builder.execute(new ItemClearingWarning(((interval * 60) - d), this))
                        .async()
                        .delay(d, TimeUnit.SECONDS)
                        .interval(interval, TimeUnit.MINUTES)
                        .name("CatClearLag Removal Warnings")
                        .submit(this));
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new EventHandler());
    }

    private void registerCommands() {

        logger.info("Registering commands...");

        CommandSpec cSpec = CommandSpec.builder()
                .description(Text.of("Remove all hostile entities from the server."))
                .permission("catclearlag.command.removehostile")
                .executor(new RemoveHostilesCommand(this))
                .build();

        CommandSpec cSpec2 = CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand(this))
                .build();

        CommandSpec cSpec3 = CommandSpec.builder()
                .description(Text.of("Remove all ground items from the server."))
                .permission("catclearlag.command.removegitems")
                .executor(new RemoveGItemsCommand(this))
                .build();
        CommandSpec cSpec4 = CommandSpec.builder()
                .description(Text.of("Force Garabage Collection"))
                .permission("catclearlag.command.forcegc")
                .executor(new ForceGCCommand(this))
                .build();

        Sponge.getCommandManager().register(this, cSpec, "removehostiles", "rhost");
        Sponge.getCommandManager().register(this, cSpec2, "removeall", "rall");
        Sponge.getCommandManager().register(this, cSpec3, "removegrounditems", "rgitems");
        Sponge.getCommandManager().register(this, cSpec4, "forcegc", "forcegarbagecollection");
    }


    public void clearGoundItems() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities();
            //for all the entities, remove the item ones
            entities.stream().filter(entity -> entity instanceof Item).forEach(Entity::remove);
        });
    }

    public Integer removeHostile() {
        final int[] i = {0};
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the hostile entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach((entity) -> {
                if (entity instanceof Hostile && !entity.getType().getId().equals("minecraft:player")) {
                    entity.remove();
                    i[0]++;
                }
            });
        });
        return i[0];
    }

    public Integer removeAll() {
        final int[] i = {0};
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach(entity -> {
                if (!entity.getType().getId().equals("minecraft:player")) {
                    entity.remove();
                    i[0]++;
                }
            });
        });
        return i[0];
    }


    public ConfigurationLoader<CommentedConfigurationNode> getCfgMgr() {
        return cfgMgr;
    }

    public static Logger getLogger() {
        return logger;
    }
}