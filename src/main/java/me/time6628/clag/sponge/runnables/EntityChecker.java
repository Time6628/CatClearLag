package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import me.time6628.clag.sponge.utils.EntityHelpers;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 1/23/2017.
 */
public class EntityChecker implements Runnable {
    private final CatClearLag plugin = CatClearLag.instance;

    @Override
    public void run() {
        if (EntityHelpers.getHostiles().size() > plugin.getHostileLimit()) {
            int affectedEntities = EntityHelpers.removeHostile();
            if (affectedEntities == -1) return;
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(Messages.getPrefix()).color(TextColors.RED).append(Text.of("Too many hostiles, removing them.")).build());
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(Messages.getPrefix()).color(TextColors.LIGHT_PURPLE).append(Text.of("Removed " + affectedEntities + " hostile entities.")).build());
        }

        if (EntityHelpers.getXPOrbs().size() > plugin.getXpOrbLimit()) {
            int affectedEntities = EntityHelpers.removeXP();
            if (affectedEntities == -1) return;
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(Messages.getPrefix()).color(TextColors.RED).append(Text.of("Too many XP Orbs, removing them.")).build());
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(Messages.getPrefix()).color(TextColors.LIGHT_PURPLE).append(Text.of("Removed " + affectedEntities + " XP orbs.")).build());
        }
    }
}
