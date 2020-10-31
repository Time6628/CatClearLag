package me.time6628.clag.sponge.commands.subcommands.laggychunks;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.commands.LaggyChunksCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by TimeTheCat on 6/26/2017.
 */
public class TilesCommand extends LaggyChunksCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("List chunks in order of most to least tiles."))
                .permission("catclearlag.command.laggychunks")
                .arguments(GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                .executor(new TilesCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Optional<World> world = args.getOne(Text.of("world"));
        List<Chunk> chunksToSort = world.isPresent() ? Lists.newArrayList(world.get().getLoadedChunks()) : plugin.getGame().getServer().getWorlds().stream().flatMap(world1 -> Streams.stream(world1.getLoadedChunks())).collect(Collectors.toList());
        TreeMap<Chunk, Integer> sortedChunks = new TreeMap<>((o1, o2) -> Integer.compare(o2.getTileEntities().size(), o1.getTileEntities().size()));
        for (Chunk chunk : chunksToSort) {
            sortedChunks.put(chunk, chunk.getTileEntities().size());
        }
        List<Text> texts = new ArrayList<>();
        sortedChunks.forEach(((chunk, integer) -> texts.add(
                Text.builder().append(Text.of(chunk.getPosition().getX() + "," + chunk.getPosition().getZ() + " contains " + integer + " tiles."))
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
}
