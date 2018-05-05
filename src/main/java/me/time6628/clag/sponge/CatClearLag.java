package me.time6628.clag.sponge;

import com.google.inject.Inject;
import me.time6628.clag.sponge.api.CCLService;
import me.time6628.clag.sponge.api.Type;
import me.time6628.clag.sponge.commands.ForceGCCommand;
import me.time6628.clag.sponge.commands.LaggyChunksCommand;
import me.time6628.clag.sponge.commands.RemoveEntitiesCommand;
import me.time6628.clag.sponge.commands.UnloadChunksCommand;
import me.time6628.clag.sponge.commands.WhiteListItemCommand;
import me.time6628.clag.sponge.config.CCLConfig;
import me.time6628.clag.sponge.config.ConfigLoader;
import me.time6628.clag.sponge.config.MessagesConfig;
import me.time6628.clag.sponge.handlers.MobEventHandler;
import me.time6628.clag.sponge.runnables.EntityChecker;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.8.3", description = "A plugin to assist in removing lag from your server.")
public class CatClearLag {
    public static CatClearLag instance;

    private final Logger logger;

    private ConfigLoader cfgLoader;

    private final File configDir;

    private final Game game;

    private final GuiceObjectMapperFactory factory;

    private MessagesConfig messages;
    private CCLConfig cclConfig;
    private final CCLService cclService = new CCLService();
    private List<Task> tasks;

    @Inject
    public CatClearLag(Logger logger, Game game, @ConfigDir(sharedRoot = false) File configDir, GuiceObjectMapperFactory factory) {
        this.logger = logger;
        this.game = game;
        this.configDir = configDir;
        this.factory = factory;
        instance = this;
    }

    public MessagesConfig getMessages() {
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
                tasks.add(builder.execute(new ItemClearingWarning(((cclConfig.interval * 60) - d)))
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
        Task.Builder builder = getGame().getScheduler().createTaskBuilder();
        tasks.add(builder.execute(new ItemClearer())
                .async()
                .delay(cclConfig.interval, TimeUnit.MINUTES)
                .interval(cclConfig.interval, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this));
        cclConfig.warnings.forEach((d) ->
                tasks.add(builder.execute(new ItemClearingWarning(((cclConfig.interval * 60) - d)))
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

    private void registerEvents() {
        if (getCclConfig().limits.perChunkLimitEnabled) Sponge.getEventManager().registerListeners(this, new MobEventHandler());
    }

    private void registerCommands() {
        getLogger().info("Registering commands...");
        Sponge.getCommandManager().register(this, RemoveEntitiesCommand.getCommand(), "re");
        Sponge.getCommandManager().register(this, ForceGCCommand.getCommand(), "forcegc", "forcegarbagecollection");
        Sponge.getCommandManager().register(this, LaggyChunksCommand.getCommand(), "laggychunks", "lc");
        Sponge.getCommandManager().register(this, WhiteListItemCommand.getCommand(), "clwhitelist", "cwl");
        Sponge.getCommandManager().register(this, UnloadChunksCommand.getCommand(), "unloadchunks", "uc");
    }


    public Integer clearGroundItems() {
        return new EntityRemover<Item>(cclService.getPredicate(Type.ITEM)).removeEntities();
    }

    public Integer removeHostile() {
        return new EntityRemover<Hostile>(cclService.getPredicate(Type.HOSTILE)).removeEntities();
    }

    public Integer removeAll() {
        return new EntityRemover<Entity>(cclService.getPredicate(Type.ALL)).removeEntities();
    }

    public Logger getLogger() {
        return logger;
    }

    public PaginationService getPaginationService() {
        return game.getServiceManager().provide(PaginationService.class).get();
    }

    public String getItemID(ItemStack si) {
        if (si.supports(Keys.ITEM_BLOCKSTATE)) {
            Optional<BlockState> bs = si.get(Keys.ITEM_BLOCKSTATE);
            if (bs.isPresent()) {
                return bs.get().getId();
            }
        }
        return si.getType().getId();
    }

    public List<Hostile> getHostiles() {
        return new EntityRemover<Hostile>(cclService.getPredicate(Type.HOSTILE)).getEntities();
    }

    public Integer removeLiving() {
        return new EntityRemover<Living>(cclService.getPredicate(Type.LIVING)).removeEntities();
    }

    public List<ExperienceOrb> getXPOrbs() {
        return new EntityRemover<ExperienceOrb>(cclService.getPredicate(Type.XP)).getEntities();
    }

    public Integer removeXP() {
        return new EntityRemover<ExperienceOrb>(cclService.getPredicate(Type.XP)).removeEntities();
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

    public Text colorMessage(String text) {
        return Text.builder().color(messages.messageColor).append(Text.of(text)).build();
    }

    public File getConfigDir() {
        return configDir;
    }

    public GuiceObjectMapperFactory getFactory() {
        return factory;
    }

}