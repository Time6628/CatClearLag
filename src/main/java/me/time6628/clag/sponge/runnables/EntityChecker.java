package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 1/23/2017.
 */
public class EntityChecker implements Runnable {
    CatClearLag plugin = CatClearLag.instance;

    @Override
    public void run() {
        if (plugin.getHostiles().size() > plugin.getHostileLimit()) {
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(plugin.getPrefix()).color(TextColors.RED).append(Text.of("Too many hostiles, removing them.")).build());
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(plugin.getPrefix()).color(TextColors.LIGHT_PURPLE).append(Text.of("Removed " + plugin.removeHostile() + " hostile entities.")).build());
        }

        if (plugin.getXPOrbs().size() > plugin.getXpOrbLimit()) {
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(plugin.getPrefix()).color(TextColors.RED).append(Text.of("Too many XP Orbs, removing them.")).build());
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(plugin.getPrefix()).color(TextColors.LIGHT_PURPLE).append(Text.of("Removed " + plugin.removeXP() + " XP orbs.")).build());
        }
    }
}
