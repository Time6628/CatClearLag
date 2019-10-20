package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import org.spongepowered.api.boss.BossBar;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.Collection;

/**
 * Created by TimeTheCat on 7/20/2016.
 */
public class ItemClearingWarning implements Runnable {

    public static ServerBossBar bossBar;

    private final int seconds;
    private final CatClearLag plugin = CatClearLag.instance;
    private final float bossBarPercent;

    public ItemClearingWarning(int seconds, float bossBarPercent) {
        this.seconds = seconds;
        this.bossBarPercent = bossBarPercent;
    }

    @Override
    public void run() {
        Text message = Messages.getWarningMsg(seconds);
        Text rawMessage = Messages.getWarningMsg(seconds, false);
        Collection<Player> onlinePlayers = plugin.getGame().getServer().getOnlinePlayers();
        if (bossBar == null || !bossBar.isVisible()) {
            bossBar = ServerBossBar.builder()
                    .color(plugin.getCclConfig().bossBar.bossBarColor)
                    .darkenSky(false)
                    .createFog(false)
                    .playEndBossMusic(false)
                    .percent(bossBarPercent)
                    .name(rawMessage)
                    .visible(true)
                    .overlay(BossBarOverlays.PROGRESS)
                    .build();
            bossBar.addPlayers(onlinePlayers);
        } else {
            bossBar.setName(rawMessage);
            bossBar.setPercent(bossBarPercent);
            bossBar.addPlayers(plugin.getGame().getServer().getOnlinePlayers());
        }
        for (Player player : onlinePlayers) {
            player.playSound(SoundTypes.ITEM_BOTTLE_FILL, SoundCategories.VOICE, player.getPosition(), 25);
            player.sendMessage(ChatTypes.ACTION_BAR, rawMessage);
        }
        plugin.getGame().getServer().getBroadcastChannel().send(message);
    }
}
