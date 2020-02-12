package me.time6628.clag.sponge.commands.subcommands.laggychunks;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.commands.LaggyChunksCommand;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by TimeTheCat on 6/26/2017.
 */
public class EntitiesCommand extends LaggyChunksCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least entities."))
                .permission("catclearlag.command.laggychunks")
                .executor(new EntitiesCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Chunk> chunksToSort = getChunks(src);
        TreeMap<Chunk, Integer> sortedChunks = new TreeMap<>((o1, o2) -> Integer.compare(o2.getEntities().size(), o1.getEntities().size()));
        for (Chunk chunk : chunksToSort) {
            sortedChunks.put(chunk, chunk.getEntities().size());
        }
        List<Text> texts = new ArrayList<>();
        sortedChunks.forEach(((chunk, integer) -> texts.add(
                Text.builder().append(Text.of(chunk.getPosition().getX() + "," + chunk.getPosition().getZ() + " contains " + integer + " entities."))
                        .onClick(callback(chunk)).build())));
        plugin.getPaginationService().builder()
                .contents((texts))
                .title(
                        Text.builder().color(TextColors.LIGHT_PURPLE)
                                .append(Text.of("Laggy Chunks"))
                                .build())
                .sendTo(src);
        return CommandResult.success();
    }

    private List<Chunk> getChunks(CommandSource source) throws CommandException{
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
