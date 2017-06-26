package me.time6628.clag.sponge.injectors;

import com.google.inject.AbstractModule;
import me.time6628.clag.sponge.CatClearLag;

/**
 * Created by TimeTheCat on 6/26/2017.
 */
public class InjectorModule extends AbstractModule {

    private final CatClearLag plugin;

    public InjectorModule(CatClearLag plugin) {
        this.plugin = plugin;
    }

    @Override protected void configure() {
        bind(CatClearLag.class).toInstance(plugin);
    }
}
