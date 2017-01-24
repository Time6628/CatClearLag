package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 1/23/2017.
 */
public class HostileChecker implements Runnable {
    CatClearLag plugin = CatClearLag.instance;

    @Override
    public void run() {
        if (plugin.getHostiles().size() > plugin.getHostileLimit()) {
            plugin.getGame().getServer().getBroadcastChannel().send(Text.builder().append(plugin.getPrefix()).color(TextColors.DARK_PURPLE).append(Text.of("Too many hostiles, removing them.")).build());
            plugin.removeHostile();
        }
    }
}
