package me.time6628.clag.sponge;

import com.google.inject.Inject;
import me.time6628.clag.sponge.api.CCLService;
import me.time6628.clag.sponge.api.Type;
import me.time6628.clag.sponge.commands.*;
import me.time6628.clag.sponge.config.CCLConfig;
import me.time6628.clag.sponge.config.ConfigLoader;
import me.time6628.clag.sponge.config.MessagesConfig;
import me.time6628.clag.sponge.handlers.ItemManager;
import me.time6628.clag.sponge.handlers.MobEventHandler;
import me.time6628.clag.sponge.runnables.EntityChecker;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.9.0", description = "A plugin to assist in removing lag from your server.")
public class CatClearLag {
    public static CatClearLag instance;

    private final Logger logger;
    private final File configDir;
    private final Game game;
    private final GuiceObjectMapperFactory factory;
    private final CCLService cclService;
    private ConfigLoader cfgLoader;
    private MessagesConfig messages;
    private CCLConfig cclConfig;
    private List<Task> tasks;
    private ItemManager itemManager;

    @Inject
    public CatClearLag(Logger logger, Game game, @ConfigDir(sharedRoot = false) File configDir, GuiceObjectMapperFactory factory) {
        this.logger = logger;
        this.game = game;
        this.configDir = configDir;
        this.factory = factory;
        instance = this;
        cclService = new CCLService();
    }

    public MessagesConfig getMessagesCfg() {
        return messages;
    }

    public CCLConfig getCclConfig() {
        return cclConfig;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        getLogger().info("Setting up config...");
        cfgLoader = new ConfigLoader(this);
        if (cfgLoader.loadConfig()) cclConfig = cfgLoader.getCclConfig();
        if (cfgLoader.loadMessages()) messages = cfgLoader.getMessagesConfig();

        game.getServiceManager().setProvider(this, CCLService.class, cclService);
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        cfgLoader = new ConfigLoader(this);
        if (cfgLoader.loadConfig()) cclConfig = cfgLoader.getCclConfig();
        if (cfgLoader.loadMessages()) messages = cfgLoader.getMessagesConfig();
        for (Task task : tasks) {
            task.cancel();
        }
        tasks = new ArrayList<>();
        Task.Builder builder = getGame().getScheduler().createTaskBuilder();
        tasks.add(builder.execute(new ItemClearer())
                .async()
                .delay(cclConfig.interval, TimeUnit.MINUTES)
                .interval(cclConfig.interval, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this));
        cclConfig.warnings.forEach((d) ->
                tasks.add(builder.execute(new ItemClearingWarning(((cclConfig.interval * 60) - d), 1.0f - ((float) cclConfig.warnings.indexOf(d)) / ((float) cclConfig.warnings.size())))
                        .async()
                        .delay(d, TimeUnit.SECONDS)
                        .interval(cclConfig.interval, TimeUnit.MINUTES)
                        .name("CatClearLag Removal Warnings")
                        .submit(this)));
        tasks.add(builder.execute(new EntityChecker())
                .async()
                .delay(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .interval(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .name("CatClearLag hostile checker")
                .submit(this));
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        getLogger().info("Starting plugin...");
        registerCommands();
        registerEvents();
        tasks = new ArrayList<>();
        if (cclConfig.liveTime.enabled) {
            this.itemManager = new ItemManager();
            getGame().getEventManager().registerListeners(this, itemManager);
            getCclService().addCheck(Type.ITEM, entity -> !itemManager.getItems().contains(entity));
        }
        Task.Builder builder = getGame().getScheduler().createTaskBuilder();
        if (getCclConfig().interval != -1) {
            tasks.add(builder.execute(new ItemClearer())
                    .async()
                    .delay(cclConfig.interval, TimeUnit.MINUTES)
                    .interval(cclConfig.interval, TimeUnit.MINUTES)
                    .name("CatClearLag Item Remover")
                    .submit(this));
            cclConfig.warnings.forEach((d) ->
            {
                logger.info("PERCENT {}", 1.0f - ((float) cclConfig.warnings.indexOf(d)) / ((float) cclConfig.warnings.size()));
                tasks.add(builder.execute(new ItemClearingWarning(((cclConfig.interval * 60) - d), 1.0f - ((float) cclConfig.warnings.indexOf(d)) / ((float) cclConfig.warnings.size())))
                        .async()
                        .delay(d, TimeUnit.SECONDS)
                        .interval(cclConfig.interval, TimeUnit.MINUTES)
                        .name("CatClearLag Removal Warnings")
                        .submit(this));
            });
        }
        tasks.add(builder.execute(new EntityChecker())
                .async()
                .delay(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .interval(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .name("CatClearLag hostile checker")
                .submit(this));
    }

    private void registerEvents() {
        if (getCclConfig().limits.perChunkLimitEnabled)
            Sponge.getEventManager().registerListeners(this, new MobEventHandler());
    }

    private void registerCommands() {
        getLogger().info("Registering commands...");
        Sponge.getCommandManager().register(this, RemoveEntitiesCommand.getCommand(), "re");
        Sponge.getCommandManager().register(this, ForceGCCommand.getCommand(), "forcegc", "forcegarbagecollection");
        Sponge.getCommandManager().register(this, LaggyChunksCommand.getCommand(), "laggychunks", "lc");
        Sponge.getCommandManager().register(this, WhiteListItemCommand.getCommand(), "clwhitelist", "cwl");
        Sponge.getCommandManager().register(this, UnloadChunksCommand.getCommand(), "unloadchunks", "uc");
    }

    public Logger getLogger() {
        return logger;
    }

    public PaginationService getPaginationService() {
        return game.getServiceManager().provide(PaginationService.class).get();
    }

    private List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Chunk> tempLoadedChunks = (Collection<Chunk>) temp.getLoadedChunks();
            chunks.addAll(tempLoadedChunks);
        });
        return chunks;
    }

    public Integer unloadChunks() {
        final int[] i = {0};
        List<Chunk> chunks = getChunks();
        chunks.forEach(chunk -> {
            chunk.unloadChunk();
            i[0]++;
        });
        return i[0];
    }

    public void addIDToWhiteList(String id) {
        logger.info("adding " + id + " to the whitelist.");
        cclConfig.whitelist.add(id);
        cfgLoader.saveConfig(cclConfig);
        cclConfig = cfgLoader.getCclConfig();
    }

    public Integer getMobLimitPerChunk() {
        return cclConfig.limits.mobLimitPerChunk;
    }

    public int getHostileLimit() {
        return cclConfig.limits.hostileLimit;
    }

    public Game getGame() {
        return game;
    }

    public int getXpOrbLimit() {
        return cclConfig.limits.maxXPOrbs;
    }

    public File getConfigDir() {
        return configDir;
    }

    public GuiceObjectMapperFactory getFactory() {
        return factory;
    }

    public CCLService getCclService() {
        return cclService;
    }
}