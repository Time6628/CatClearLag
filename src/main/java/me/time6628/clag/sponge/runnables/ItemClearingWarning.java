package me.time6628.clag.sponge.runnables;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 7/20/2016.
 */
public class ItemClearingWarning implements Runnable {

    private int seconds;
    private Game game;

    public ItemClearingWarning(int seconds, Game game) {
        this.seconds = seconds;
        this.game = game;
    }

    @Override
    public void run() {
        game.getServer().getBroadcastChannel().send(Text.of(TextColors.RED + "All ground items will be cleared in" + seconds + "."));
    }
}
