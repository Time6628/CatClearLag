package me.time6628.clag.sponge.utils;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;

import java.util.List;

public class ChunkHelper {
    public static List<Chunk> getChunks(CommandSource source) throws CommandException {
        if (source instanceof Player) {
            return (List<Chunk>) ((Player) source).getWorld().getLoadedChunks();
        } else {
            final Server server =  Sponge.getServer();
            return (List<Chunk>) server.getWorld(server.getDefaultWorldName())
                    .orElseThrow(()->new CommandException(Text.of("Failed to get default world!")))
                    .getLoadedChunks();
        }
    }
}
