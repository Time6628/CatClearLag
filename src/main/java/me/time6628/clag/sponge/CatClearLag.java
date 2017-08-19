package me.time6628.clag.sponge;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.time6628.clag.sponge.commands.ForceGCCommand;
import me.time6628.clag.sponge.commands.LaggyChunksCommand;
import me.time6628.clag.sponge.commands.RemoveEntitiesCommand;
import me.time6628.clag.sponge.commands.UnloadChunksCommand;
import me.time6628.clag.sponge.commands.WhiteListItemCommand;
import me.time6628.clag.sponge.commands.subcommands.laggychunks.EntitiesCommand;
import me.time6628.clag.sponge.commands.subcommands.laggychunks.TilesCommand;
import me.time6628.clag.sponge.commands.subcommands.removeentities.*;
import me.time6628.clag.sponge.config.CCLConfig;
import me.time6628.clag.sponge.config.ConfigLoader;
import me.time6628.clag.sponge.config.MessagesConfig;
import me.time6628.clag.sponge.handlers.MobEventHandler;
import me.time6628.clag.sponge.runnables.EntityChecker;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.8.1", description = "A plugin to assist in removing lag from your server.")
public class CatClearLag {
    public static CatClearLag instance;

    private Logger logger;

    private ConfigLoader cfgLoader;

    private File configDir;

    private Game game;

    private GuiceObjectMapperFactory factory;

    private MessagesConfig messages;
    private CCLConfig cclConfig;

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


    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        getLogger().info("Starting plugin...");
        registerCommands();
        registerEvents();
        Task.Builder builder = getGame().getScheduler().createTaskBuilder();
        builder.execute(new ItemClearer())
                .async()
                .delay(cclConfig.interval, TimeUnit.MINUTES)
                .interval(cclConfig.interval, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);
        cclConfig.warnings.forEach((d) ->
                builder.execute(new ItemClearingWarning(((cclConfig.interval * 60) - d)))
                        .async()
                        .delay(d, TimeUnit.SECONDS)
                        .interval(cclConfig.interval, TimeUnit.MINUTES)
                        .name("CatClearLag Removal Warnings")
                        .submit(this));
        builder.execute(new EntityChecker())
                .async()
                .delay(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .interval(cclConfig.limits.entityCheckInterval, TimeUnit.MINUTES)
                .name("CatClearLag hostile checker")
                .submit(this);
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new MobEventHandler());
    }

    private void registerCommands() {

        getLogger().info("Registering commands...");

        CommandSpec cSpecRH = CommandSpec.builder()
                .description(Text.of("Remove all hostile entities from the server."))
                .permission("catclearlag.command.removehostile")
                .executor(new RemoveHostilesCommand())
                .build();

        CommandSpec cSpecRA = CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand())
                .build();

        CommandSpec cSpecRG = CommandSpec.builder()
                .description(Text.of("Remove all ground items from the server."))
                .permission("catclearlag.command.removegitems")
                .executor(new RemoveGItemsCommand())
                .build();

        CommandSpec cSpecRL = CommandSpec.builder()
                .description(Text.of("Remove all living entities from the server."))
                .permission("catclearlag.command.removelving")
                .executor(new RemoveLivingCommand())
                .build();

        CommandSpec cSpecRXP = CommandSpec.builder()
                .description(Text.of("Remove all XP Orbs from the server."))
                .permission("catclearlag.command.removexp")
                .executor(new RemoveXPCommand())
                .build();

        CommandSpec re = CommandSpec.builder()
                .description(Text.of("Remove various types of entities."))
                .permission("catclearlag.command.removeentities")
                .child(cSpecRH, "hostiles", "host", "h")
                .child(cSpecRA, "all", "a")
                .child(cSpecRG, "items", "i")
                .child(cSpecRL, "living", "l")
                .child(cSpecRXP, "xp", "x")
                .executor(new RemoveEntitiesCommand())
                .build();

        CommandSpec cSpec4 = CommandSpec.builder()
                .description(Text.of("Force Garabage Collection"))
                .permission("catclearlag.command.forcegc")
                .executor(new ForceGCCommand())
                .build();

        CommandSpec cSpecLCE = CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least entities."))
                .permission("catclearlag.command.laggychunks")
                .executor(new EntitiesCommand())
                .build();

        CommandSpec cSpecLCT = CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least tiles."))
                .permission("catclearlag.command.laggychunks")
                .executor(new TilesCommand())
                .build();

        CommandSpec cSpecLC = CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least entities or tiles."))
                .permission("catclearlag.command.laggychunks")
                .executor(new LaggyChunksCommand())
                .child(cSpecLCE, "entities", "e")
                .child(cSpecLCT, "tiles", "t")
                .build();

        CommandSpec cSpec6 = CommandSpec.builder()
                .description(Text.of("Add an itemtype to the clearlag whitelist"))
                .permission("catclearlag.command.whitelistitem")
                .executor(new WhiteListItemCommand())
                .arguments(GenericArguments.optional(GenericArguments.catalogedElement(Text.of("item"), ItemType.class)))
                .build();

        CommandSpec cSpecUC = CommandSpec.builder()
                .description(Text.of("Force all chunks to unload."))
                .permission("catclearlag.command.unloadchunks")
                .executor(new UnloadChunksCommand())
                .build();

        Sponge.getCommandManager().register(this, re, "re");
        Sponge.getCommandManager().register(this, cSpec4, "forcegc", "forcegarbagecollection");
        Sponge.getCommandManager().register(this, cSpecLC, "laggychunks", "lc");
        Sponge.getCommandManager().register(this, cSpec6, "clwhitelist", "cwl");
        Sponge.getCommandManager().register(this, cSpecUC, "unloadchunks", "uc");
    }


    public Integer clearGroundItems() {
        final int[] i = {0};
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities().stream().filter(entity -> entity instanceof Item).collect(Collectors.toList());
            //for all the entities, remove the item ones
            entities.forEach((entity -> {
                Item entityItem = (Item) entity;
                Optional<BlockType> type = entityItem.getItemType().getBlock();
                String id;
                id = type.map(blockType -> blockType.getDefaultState().getId()).orElseGet(() -> entityItem.getItemType().getId());
                i[0]++;
                if (!cclConfig.entityWhiteList.contains(id)) entity.remove();
            }));
        });
        return i[0];
    }

    public Integer removeHostile() {
        final int[] i = {0};
        List<Hostile> hostiles = getHostiles();
        hostiles.forEach((entity) -> {
            entity.remove();
            i[0]++;
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
            Collection<Entity> entities = temp.getEntities().stream().filter(entity -> !(entity instanceof Player)).collect(Collectors.toList());
            //remove them all
            entities.forEach(entity -> {
                if (!cclConfig.entityWhiteList.contains(entity.getType().getId())) {
                    entity.remove();
                    i[0]++;
                }
            });
        });
        return i[0];
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
        return si.getItem().getId();
    }

    public List<Hostile> getHostiles() {
        EntityRemover<Hostile> er = new EntityRemover<>();
        return er.getEntitys();
    }

    public List<Entity> getLiving() {
        List<Entity> liv = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities().stream().filter(entity -> entity instanceof Living && !(entity instanceof Player)).collect(Collectors.toList());

            liv.addAll(entities);
        });
        return liv;
    }

    public Integer removeLiving() {
        final int[] i = {0};
        List<Entity> hostiles = getLiving();
        hostiles.forEach((entity) -> {
            entity.remove();
            i[0]++;
        });
        return i[0];
    }

    public List<Entity> getXPOrbs() {
        List<Entity> xp = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities().stream().filter(entity -> entity instanceof ExperienceOrb && !(entity instanceof Player)).collect(Collectors.toList());
            xp.addAll(entities);
        });
        return xp;
    }

    public Integer removeXP() {
        final int[] i = {0};
        List<Entity> ents = getXPOrbs();
        ents.forEach((entity) -> {
            entity.remove();
            i[0]++;
        });
        return i[0];
    }

    public List<Chunk> getChunks() {
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
    }

    public void addEntityIDToWhitelist(String id) {
        logger.info("adding " + id + " to the entity whitelist.");
        cclConfig.entityWhiteList.add(id);
        cfgLoader.saveConfig(cclConfig);
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