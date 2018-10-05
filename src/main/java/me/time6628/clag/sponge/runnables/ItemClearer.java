package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import me.time6628.clag.sponge.utils.EntityHelpers;

/**
 * Created by pturc_000 on 7/13/2016.
 */
public class ItemClearer implements Runnable {
    private final CatClearLag plugin = CatClearLag.instance;

    @Override
    public void run() {
        int i = EntityHelpers.clearGroundItems();
        //broadcast that they have all been removed
        plugin.getGame().getServer().getBroadcastChannel().send(Messages.getClearMsg(i));
    }
}
