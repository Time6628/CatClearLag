package me.time6628.clag.sponge;

import com.google.inject.Inject;

import me.time6628.clag.sponge.commands.*;
import me.time6628.clag.sponge.handlers.MobEventHandler;
import me.time6628.clag.sponge.runnables.HostileChecker;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.6.5", description = "DIE LAG, DIE!")
public class CatClearLag {

    public static CatClearLag instance;


    @Inject
    private org.slf4j.Logger logger;

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
    private Scheduler scheduler;
    private int interval = 0;
    private List<Integer> warning;
    private List<String> whitelistItemsAsStrings = new ArrayList<>();
    private List<ItemType> whitelistedItems = new ArrayList<>();
    private Integer mobLimitPerChunk = 20;
    private int hostileLimit;
    private int hostileLimitInterval;

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
                this.cfg.getNode("prefix").setValue(TextSerializers.FORMATTING_CODE.serialize(Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build()));
                this.cfg.getNode("whitelist").setValue(new ArrayList<String>(){{add(ItemTypes.DIAMOND.getId());}});
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "hostile-entity-check-interval").setValue(10);

                getLogger().info("Config created.");
                getCfgMgr().save(cfg);
            }

            this.cfg = getCfgMgr().load();

            if (this.cfg.getNode("version").getDouble() == 0.1) {
                logger.info("Outdated config... adding new options...");
                //2.0
                this.cfg.getNode("whitelist").setValue(new ArrayList<String>() {{
                    add(ItemTypes.DIAMOND.getId());
                }});
                //this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);

                //3.0
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "hostile-entity-check-interval").setValue(10);
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                //version
                this.cfg.getNode("version").setValue(0.3);
                getCfgMgr().save(cfg);
            } else if (this.cfg.getNode("version").getDouble() == 0.2) {
                //3.0
                this.cfg.getNode("limits", "hostile-limit").setValue(500);
                this.cfg.getNode("limits", "hostile-entity-check-interval").setValue(10);
                this.cfg.getNode("limits", "mob-limit-per-chunk").setValue(20);
                //version
                this.cfg.getNode("version").setValue(0.3);
                getCfgMgr().save(cfg);
            } else {
                logger.info("Config up to date!");
            }

            this.prefix = TextSerializers.FORMATTING_CODE.deserialize(cfg.getNode("prefix").getString());
            this.interval = cfg.getNode("interval").getInt();
            this.warning = cfg.getNode("warnings").getList(o -> (Integer) o);

            whitelistItemsAsStrings = cfg.getNode("whitelist").getList(o -> (String) o);

            for (String s : whitelistItemsAsStrings) {
                Optional<ItemType> a = getItemTypeFromString(s);
                a.ifPresent(itemType -> whitelistedItems.add(itemType));
            }

            this.mobLimitPerChunk = this.cfg.getNode("limits", "mob-limit-per-chunk").getInt();
            this.hostileLimit = this.cfg.getNode("limits", "hostile-limit").getInt();
            this.hostileLimitInterval = this.cfg.getNode("limits", "hostile-entity-check-interval").getInt();


            scheduler = game.getScheduler();
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
        Task.Builder builder = scheduler.createTaskBuilder();
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
        builder.execute(new HostileChecker())
                .async()
                .delay(hostileLimitInterval, TimeUnit.MINUTES)
                .interval(interval, TimeUnit.MINUTES)
                .name("CatClearLag hostile checker")
                .submit(this);
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new MobEventHandler());
    }

    private void registerCommands() {

        getLogger().info("Registering commands...");

        CommandSpec cSpec = CommandSpec.builder()
                .description(Text.of("Remove all hostile entities from the server."))
                .permission("catclearlag.command.removehostile")
                .executor(new RemoveHostilesCommand())
                .build();

        CommandSpec cSpec2 = CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand())
                .build();

        CommandSpec cSpec3 = CommandSpec.builder()
                .description(Text.of("Remove all ground items from the server."))
                .permission("catclearlag.command.removegitems")
                .executor(new RemoveGItemsCommand())
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

        Sponge.getCommandManager().register(this, cSpec, "removehostiles", "rhost");
        Sponge.getCommandManager().register(this, cSpec2, "removeall", "rall");
        Sponge.getCommandManager().register(this, cSpec3, "removegrounditems", "rgitems");
        Sponge.getCommandManager().register(this, cSpec4, "forcegc", "forcegarbagecollection");
        Sponge.getCommandManager().register(this, cSpec5, "laggychunks", "lc");
        //Sponge.getCommandManager().register(this, cSpec6, "clwhitelist", "cwl");
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
                if (whitelistedItems.contains(entityItem.getItemType())) return;
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

    public Logger getLogger() {
        return logger;
    }

    public PaginationService getPaginationService() {
        return game.getServiceManager().provide(PaginationService.class).get();
    }

    private Optional<ItemType> getItemTypeFromString(String id) {
        return game.getRegistry().getType(ItemType.class, id);
    }

    public void addItemIDToWhiteList(ItemType type) {
        try {
            this.cfg.getNode("whitelist").setValue(getWhitelistItemsAsStrings().add(type.getId()));
            this.cfgMgr.save(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getWhitelistedItems().add(type);
    }

    public List<Entity> getHostiles() {
        List<Entity> hosts = new ArrayList<>();
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the hostile entities in the world
            Collection<Entity> entities = temp.getEntities();
            //get them all
            entities.forEach((entity) -> {
                if (entity instanceof Hostile && !entity.getType().getId().equals("minecraft:player")) {
                    hosts.add(entity);
                }
            });
        });
        return hosts;
    }

    public List<ItemType> getWhitelistedItems() {
        return this.whitelistedItems;
    }

    public List<String> getWhitelistItemsAsStrings() {
        return this.whitelistItemsAsStrings;
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
}