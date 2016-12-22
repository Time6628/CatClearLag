package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.*;

/**
 * Created by TimeTheCat on 12/18/2016.
 */
public class LaggyChunksCommand implements CommandExecutor {
    private CatClearLag plugin;
    public LaggyChunksCommand(CatClearLag catClearLag) {
        this.plugin = catClearLag;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Chunk> chunksToSort = (List<Chunk>) ((Player) src).getWorld().getLoadedChunks();
        Map<Chunk, Integer> sortedChunks = new TreeMap<>((o1, o2) -> Integer.compare(o1.getEntities().size(), o2.getEntities().size()));
        for (Chunk chunk : chunksToSort) {
            sortedChunks.put(chunk, chunk.getEntities().size());
        }
        List<Text> texts = new ArrayList<>();
        sortedChunks.forEach(((chunk, integer) -> texts.add(Text.builder().append(Text.of(chunk.getPosition().getX() + "," + chunk.getPosition().getZ() + " contains " + integer + " entities.")).build())));
        plugin.getPaginationService().builder().contents((texts)).title(Text.builder().color(TextColors.LIGHT_PURPLE).append(Text.of("Laggy Chunks")).build()).sendTo(src);
        return CommandResult.empty();
    }
}
