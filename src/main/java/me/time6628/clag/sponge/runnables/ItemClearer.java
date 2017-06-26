package me.time6628.clag.sponge.runnables;

import com.google.inject.Inject;
import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Texts;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.CollectionUtils;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by pturc_000 on 7/13/2016.
 */
public class ItemClearer implements Runnable {
    private CatClearLag plugin = CatClearLag.instance;

    @Override
    public void run() {
        int i = plugin.clearGroundItems();
        //broadcast that they have all been removed
        plugin.getGame().getServer().getBroadcastChannel().send(Texts.clearMsg(i));
    }
}
