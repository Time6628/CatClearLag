package me.time6628.clag.sponge.config;

import com.google.common.reflect.TypeToken;
import me.time6628.clag.sponge.CatClearLag;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;

public class ConfigLoader {

    private final CatClearLag plugin;

    private CCLConfig cclConfig;
    private ConfigurationLoader<CommentedConfigurationNode> cclLoader;

    private MessagesConfig messagesConfig;

    public ConfigLoader(CatClearLag pl) {
        this.plugin = pl;
        if (!plugin.getConfigDir().exists()) {
            plugin.getConfigDir().mkdirs();
        }
    }

    public boolean loadConfig() {
        try {
            File file = new File(plugin.getConfigDir(), "catclearlag.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            cclLoader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = cclLoader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            cclConfig = config.getValue(TypeToken.of(CCLConfig.class), new CCLConfig());
            cclLoader.save(config);
            return true;
        } catch (Exception e) {
            plugin.getLogger().error("Could not load config.", e);
            return false;
        }
    }

    public boolean loadMessages() {
        try {
            File file = new File(plugin.getConfigDir(), "messages.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            messagesConfig = config.getValue(TypeToken.of(MessagesConfig.class), new MessagesConfig());
            loader.save(config);
            return true;
        } catch (Exception e) {
            plugin.getLogger().error("Could not load config.", e);
            return false;
        }
    }

    public void saveConfig(CCLConfig newConfig) {
        try {
            File file = new File(plugin.getConfigDir(), "catclearlag.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            CommentedConfigurationNode config = cclLoader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            config.setValue(TypeToken.of(CCLConfig.class), newConfig);
            cclLoader.save(config);
        } catch (Exception e) {
            plugin.getLogger().error("Could not load config.", e);
        }
    }

    public CCLConfig getCclConfig() {
        return cclConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }
}
