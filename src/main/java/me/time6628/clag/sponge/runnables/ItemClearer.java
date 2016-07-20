package me.time6628.clag.sponge.runnables;

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
    private Game game;
    public ItemClearer(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        //get all the worlds
        Collection<World> worlds = game.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities(Predicate.isEqual(EntityTypes.ITEM));
            //remove them all
            entities.forEach(Entity::remove);
        });
        //broadcast that they have all been removed
        game.getServer().getBroadcastChannel().send(Text.of(TextColors.RED + "All ground items have been cleared."));
    }
}
