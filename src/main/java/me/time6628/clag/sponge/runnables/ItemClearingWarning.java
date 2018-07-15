package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;

import java.util.Collections;

/**
 * Created by TimeTheCat on 7/20/2016.
 */
public class ItemClearingWarning implements Runnable {

    private final int seconds;
    private final CatClearLag plugin = CatClearLag.instance;

    public ItemClearingWarning(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {
        plugin.getGame().getServer().getBroadcastChannel().send(Messages.getWarningMsg(seconds));
    }
}
