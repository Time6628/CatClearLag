package me.time6628.clag.sponge.runnables;

import com.google.inject.Inject;
import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Texts;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 7/20/2016.
 */
public class ItemClearingWarning implements Runnable {

    private int seconds;
    @Inject private CatClearLag plugin;

    public ItemClearingWarning(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {
        plugin.getGame().getServer().getBroadcastChannel().send(Texts.warningMessage(seconds));
    }
}
