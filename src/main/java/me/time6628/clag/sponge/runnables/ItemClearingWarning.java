package me.time6628.clag.sponge.runnables;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 7/20/2016.
 */
public class ItemClearingWarning implements Runnable {

    public static ServerBossBar bossBar;
    public static SpongeExecutorService.SpongeFuture<?> bossBarUpdater;

    private final int seconds;
    private final CatClearLag plugin = CatClearLag.instance;

    public ItemClearingWarning(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void run() {
        Text message = Messages.getWarningMsg(seconds);
        Text rawMessage = Messages.getWarningMsg(seconds, false);
        Collection<Player> onlinePlayers = plugin.getGame().getServer().getOnlinePlayers();
        if (plugin.isBossBarEnabled()) {
            if (bossBar == null || !bossBar.isVisible()) {
                bossBar = ServerBossBar.builder()
                        .color(plugin.getCclConfig().bossBar.bossBarColor)
                        .darkenSky(false)
                        .createFog(false)
                        .playEndBossMusic(false)
                        .percent(1.0f)
                        .name(rawMessage)
                        .visible(true)
                        .overlay(BossBarOverlays.PROGRESS)
                        .build();
                bossBar.addPlayers(onlinePlayers);
                bossBarUpdater = plugin.getGame().getScheduler().createAsyncExecutor(plugin).scheduleAtFixedRate(() -> {
                    if (bossBar != null) {
                        bossBar.setPercent(bossBar.getPercent() - (1.0f / seconds));
                        bossBar.setName(Messages.getWarningMsg((int) (seconds * bossBar.getPercent()), false));
                    }
                }, 1, 1, TimeUnit.SECONDS);
            }
        }
        for (Player player : onlinePlayers) {
            if (plugin.getCclConfig().sounds.enabled)
                player.playSound(plugin.getCclConfig().sounds.warningSound, plugin.getCclConfig().sounds.soundCategory, player.getPosition(), 25);
            if (plugin.getMessagesCfg().actionBar) player.sendMessage(ChatTypes.ACTION_BAR, rawMessage);
        }
        plugin.getGame().getServer().getBroadcastChannel().send(message);
    }
}
