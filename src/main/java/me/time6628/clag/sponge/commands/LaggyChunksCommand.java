package me.time6628.clag.sponge.commands;

import com.flowpowered.math.vector.Vector3d;
import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

/**
 * Created by TimeTheCat on 12/18/2016.
 */
public class LaggyChunksCommand implements CommandExecutor {
    private CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> arg = args.getOne(Text.of("entity"));
        if (arg.isPresent()) {
            if (arg.get().toLowerCase().equals("e")) {
                laggyChunksEntity(src);
            } else if (arg.get().toLowerCase().equals("te")) {
                laggyChunksTiles(src);
            }
        }

        return CommandResult.success();
    }

    private ClickAction.ExecuteCallback callback(Chunk chunk, CommandSource source) {
        return TextActions.executeCallback((commandSource -> {
            Player player = (Player) commandSource;
            Location<World> a = new Location<>(chunk.getWorld(), chunk.getPosition());
            Location<World> b = new Location<>(a.getExtent(), a.getX() * 16, a.getExtent().getBlockMax().getY(), a.getZ()  * 16);
            Optional<BlockRayHit<World>> c = BlockRay.from(b).stopFilter(BlockRay.onlyAirFilter()).to(a.getPosition().sub(a.getX(), 1, a.getZ())).end();

            if (c.isPresent()) {
                BlockRayHit<World> d = c.get();
                player.setLocation(d.getLocation());
            } else {
                source.sendMessage(Text.of("Could not send you to: " + a.getX() + "," + a.getZ()));
            }
            //player.setLocation(a);
        }));
    }

    void laggyChunksEntity(CommandSource src) {
        List<Chunk> chunksToSort = (List<Chunk>) ((Player) src).getWorld().getLoadedChunks();
        TreeMap<Chunk, Integer> sortedChunks = new TreeMap<>((o1, o2) -> Integer.compare(o2.getEntities().size(), o1.getEntities().size()));
        for (Chunk chunk : chunksToSort) {
            sortedChunks.put(chunk, chunk.getEntities().size());
        }
        List<Text> texts = new ArrayList<>();
        sortedChunks.forEach(((chunk, integer) -> texts.add(
                Text.builder().append(Text.of(chunk.getPosition().getX() + "," + chunk.getPosition().getZ() + " contains " + integer + " entities."))
                        .onClick(callback(chunk, src)).build())));
        plugin.getPaginationService().builder()
                .contents((texts))
                .title(
                        Text.builder().color(TextColors.LIGHT_PURPLE)
                                .append(Text.of("Laggy Chunks"))
                                .build())
                .sendTo(src);
    }

    void laggyChunksTiles(CommandSource src) {
        List<Chunk> chunksToSort = (List<Chunk>) ((Player) src).getWorld().getLoadedChunks();
        TreeMap<Chunk, Integer> sortedChunks = new TreeMap<>((o1, o2) -> Integer.compare(o2.getTileEntities().size(), o1.getTileEntities().size()));
        for (Chunk chunk : chunksToSort) {
            sortedChunks.put(chunk, chunk.getTileEntities().size());
        }
        List<Text> texts = new ArrayList<>();
        sortedChunks.forEach(((chunk, integer) -> texts.add(
                Text.builder().append(Text.of(chunk.getPosition().getX() + "," + chunk.getPosition().getZ() + " contains " + integer + " tiles."))
                        .onClick(callback(chunk, src)).build())));
        plugin.getPaginationService().builder()
                .contents((texts))
                .title(
                        Text.builder().color(TextColors.LIGHT_PURPLE)
                                .append(Text.of("Laggy Chunks"))
                                .build())
                .sendTo(src);
    }
}
