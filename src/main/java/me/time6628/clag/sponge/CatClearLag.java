package me.time6628.clag.sponge;

import com.google.inject.Inject;
import me.time6628.clag.sponge.commands.ForceGCCommand;
import me.time6628.clag.sponge.commands.LaggyChunksCommand;
import me.time6628.clag.sponge.commands.RemoveEntitiesCommand;
import me.time6628.clag.sponge.commands.WhiteListItemCommand;
import me.time6628.clag.sponge.commands.subcommands.removeentities.*;
import me.time6628.clag.sponge.handlers.MobEventHandler;
import me.time6628.clag.sponge.runnables.EntityChecker;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.7.1", description = "DIE LAG, DIE!")
public class CatClearLag {

    public static CatClearLag instance;


    @Inject
    private Logger logger;

    //config stuff
    private ConfigurationNode cfg;

    @Inject
    PluginContainer pluginContainer;

    //config stuff
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultCfg;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> cfgMgr;

    //public
    @Inject
    private Game game;

    private Text prefix;

    //private
    //private Scheduler scheduler;
    private int interval = 0;
    private List<Integer> warning;
    private List<String> whitelistItemsAsStrings;
    private List<String> whitelistedItems;
    private Integer mobLimitPerChunk = 20;
    private int hostileLimit;
    private int limitInterval;
    private TextColor messageColor;
    private TextColor warningColor;
    private int xpOrbLimit;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        getLogger().info("Making config...");
        try {
            if (!defaultCfg.exists()) {
                defaultCfg.createNewFile();

                this.cfg = getCfgMgr().load();

                this.cfg.getNode("Version").setValue(0.3);

                this.cfg.getNode("interval").setValue(10);
                this.cfg.getNode("warnings").setValue(new ArrayList<Integer>(){{add(540);add(570);}});
                this.cfg.getNode("whitelist").setValue(new ArrayList<String>(){{add(ItemTypes.DIAMOND.getId());add(BlockTypes.DIAMOND_BLOCK.getId());add(BlockTypes.BEACON.getDefaultState().getId());}});
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "entity-check-interval").setValue(5);
                this.cfg.getNode("limits", "xp-orb-limit").setValue(300);
                this.cfg.getNode("messages", "message-color").setValue(TextColors.LIGHT_PURPLE.getId());
                this.cfg.getNode("messages", "prefix").setValue(TextSerializers.FORMATTING_CODE.serialize(Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build()));
                this.cfg.getNode("messages", "warning-message-color").setValue(TextColors.RED.getId());

                getLogger().info("Config created.");
                getCfgMgr().save(cfg);
            }

            this.cfg = getCfgMgr().load();

            if (this.cfg.getNode("version").getDouble() == 0.1) {
                logger.info("Outdated config... adding new options...");
                //2.0
                this.cfg.getNode("whitelist").setValue(new ArrayList<String>() {{
                    add(ItemTypes.DIAMOND.getId());
                    add(BlockTypes.DIAMOND_BLOCK.getId());
                    add(BlockTypes.BEACON.getDefaultState().getId());
                }});
                //this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);

                //3.0
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "entity-check-interval").setValue(5);
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                this.cfg.getNode("limits", "xp-orb-limit").setValue(300);
                this.cfg.getNode("messages", "message-color").setValue(TextColors.LIGHT_PURPLE.getId());
                this.cfg.getNode("messages", "prefix").setValue(Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build());
                this.cfg.getNode("messages", "warning-message-color").setValue(TextColors.RED.getId());

                //version
                this.cfg.getNode("version").setValue(0.3);
                getCfgMgr().save(cfg);
            } else if (this.cfg.getNode("version").getDouble() == 0.2) {
                //3.0
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "entity-check-interval").setValue(5);
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                this.cfg.getNode("limits", "xp-orb-limit").setValue(300);
                this.cfg.getNode("messages", "message-color").setValue(TextColors.LIGHT_PURPLE.getId());
                this.cfg.getNode("messages", "warning-message-color").setValue(TextColors.RED.getId());
                this.cfg.getNode("messages", "prefix").setValue(Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build());

                //version
                this.cfg.getNode("version").setValue(0.3);
                getCfgMgr().save(cfg);
            } else {
                logger.info("Config up to date!");
            }
            //messages
            this.prefix = TextSerializers.FORMATTING_CODE.deserialize(cfg.getNode("messages", "prefix").getString());
            Optional<TextColor> t = getColorFromID(this.cfg.getNode("messages", "message-color").getString());
            Optional<TextColor> w = getColorFromID(this.cfg.getNode("messages", "warning-message-color").getString());
            this.messageColor = t.orElse(TextColors.LIGHT_PURPLE);
            this.warningColor = w.orElse(TextColors.RED);

            this.interval = cfg.getNode("interval").getInt();
            this.warning = cfg.getNode("warnings").getList(o -> (Integer) o);

            //whitelist
            whitelistItemsAsStrings = cfg.getNode("whitelist").getList(o -> (String) o);
            whitelistedItems = new ArrayList<>();
            for (String s : whitelistItemsAsStrings) {
                Optional<String> a = getItemIDFromString(s);
                a.ifPresent(type -> whitelistedItems.add(type));
            }

            //limits
            this.mobLimitPerChunk = this.cfg.getNode("limits", "mob-limit-per-chunk").getInt();
            this.hostileLimit = this.cfg.getNode("limits", "hostile-limit").getInt();
            this.limitInterval = this.cfg.getNode("limits", "hostile-entity-check-interval").getInt();
            this.xpOrbLimit = this.cfg.getNode("limits", "xp-orb-limit").getInt();

        } catch (Exception e) {
            e.printStackTrace();
        }

        pluginContainer.getInstance().ifPresent( a -> {
            instance = (CatClearLag) a;
        });
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        getLogger().info("Starting plugin...");
        registerCommands();
        registerEvents();
        Task.Builder builder = getGame().getScheduler().createTaskBuilder();
        builder.execute(new ItemClearer())
                .async()
                .delay(interval, TimeUnit.MINUTES)
                .interval(interval, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);
        warning.forEach((d) ->
                builder.execute(new ItemClearingWarning(((interval * 60) - d)))
                        .async()
                        .delay(d, TimeUnit.SECONDS)
                        .interval(interval, TimeUnit.MINUTES)
                        .name("CatClearLag Removal Warnings")
                        .submit(this));
        builder.execute(new EntityChecker())
                .async()
                .delay(limitInterval, TimeUnit.MINUTES)
                .interval(interval, TimeUnit.MINUTES)
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
                .child(cSpecRH, "hostiles", "host")
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

        CommandSpec cSpec5 = CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least entities."))
                .permission("catclearlag.command.laggychunks")
                .executor(new LaggyChunksCommand())
                .arguments(GenericArguments.choices(Text.of("entity"), new HashMap<String, String>(){{put("entities", "e");put("tiles", "te");}}))
                .build();

        CommandSpec cSpec6 = CommandSpec.builder()
                .description(Text.of("Add an itemtype to the clearlag whitelist"))
                .permission("catclearlag.command.whitelistitem")
                .executor(new WhiteListItemCommand())
                .build();

        Sponge.getCommandManager().register(this, re, "re");
        Sponge.getCommandManager().register(this, cSpec4, "forcegc", "forcegarbagecollection");
        Sponge.getCommandManager().register(this, cSpec5, "laggychunks", "lc");
        Sponge.getCommandManager().register(this, cSpec6, "clwhitelist", "cwl");
    }


    public void clearGoundItems() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities();
            //for all the entities, remove the item ones
            entities.stream().filter(entity -> entity instanceof Item).forEach((entity -> {
                Item entityItem = (Item) entity;
                Optional<BlockType> type = entityItem.getItemType().getBlock();
                String id;
                id = type.map(blockType -> blockType.getDefaultState().getId()).orElseGet(() -> entityItem.getItemType().getId());

                if (whitelistedItems.contains(id)) return;
                else entity.remove();
            }));
        });
    }

    public Integer removeHostile() {
        final int[] i = {0};
        List<Entity> hostiles = getHostiles();
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
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach(entity -> {
                if (!entity.getType().equals(EntityTypes.PLAYER)) {
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

    public Logger getLogger() {
        return logger;
    }

    public PaginationService getPaginationService() {
        return game.getServiceManager().provide(PaginationService.class).get();
    }

    private Optional<String> getItemIDFromString(String id) {
        Optional<ItemType> type = game.getRegistry().getType(ItemType.class, id);
        if (type.isPresent()) {
            ItemType ii = type.get();
            if (ii.getBlock().isPresent()) {
                BlockState bt = ii.getBlock().get().getDefaultState();
                return Optional.of(bt.getId());
            } else {
                return Optional.of(ii.getId());
            }
        }
        return Optional.empty();
    }

    public List<Entity> getHostiles() {
        List<Entity> hosts = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the hostile entities in the world
            Collection<Entity> entities = temp.getEntities();

            hosts.addAll(entities.stream().filter(entity -> entity instanceof Hostile).filter(entity -> !entity.getType().equals(EntityTypes.PLAYER)).collect(Collectors.toList()));
        });
        return hosts;
    }

    public List<Entity> getLiving() {
        List<Entity> liv = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities();

            liv.addAll(entities.stream().filter(entity -> entity instanceof Living).filter(entity -> !entity.getType().equals(EntityTypes.PLAYER)).collect(Collectors.toList()));
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
            Collection<Entity> entities = temp.getEntities();
            xp.addAll(entities.stream().filter(entity -> entity instanceof ExperienceOrb).filter(entity -> !entity.getType().equals(EntityTypes.PLAYER)).collect(Collectors.toList()));
        });
        return xp;
    }

    public Integer removeXP() {
        final int[] i = {0};
        List<Entity> hostiles = getXPOrbs();
        hostiles.forEach((entity) -> {
            entity.remove();
            i[0]++;
        });
        return i[0];
    }

    public void addIDToWhiteList(String id) {
        try {
            logger.info("adding " + id + " to the whitelist.");
            whitelistedItems.add(id);
            cfg.getNode("whitelist").setValue(whitelistedItems);
            cfgMgr.save(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getWhitelistedItems() {
        return this.whitelistedItems;
    }

    public ConfigurationNode getCfg() {
        return cfg;
    }

    public Integer getMobLimitPerChunk() {
        return mobLimitPerChunk;
    }

    public int getHostileLimit() {
        return hostileLimit;
    }

    public Text getPrefix() {
        return prefix;
    }

    public Game getGame() {
        return game;
    }

    public int getXpOrbLimit() {
        return xpOrbLimit;
    }

    public Text colorMessage(String text) {
        return Text.builder().color(getMessageColor()).append(Text.of(text)).build();
    }

    public Text colorWarningMessage(String text) {
        return Text.builder().color(getWarningColor()).append(Text.of(text)).build();
    }

    public Optional<TextColor> getColorFromID(String id) {
        return game.getRegistry().getType(TextColor.class, id);
    }

    public TextColor getMessageColor() {
        return messageColor;
    }
    
    public TextColor getWarningColor() {
        return warningColor;
    }
}