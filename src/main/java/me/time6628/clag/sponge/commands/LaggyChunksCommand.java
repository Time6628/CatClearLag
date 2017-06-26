package me.time6628.clag.sponge.commands;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
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
        plugin.getPaginationService().builder().contents(getCommands()).title(Text.builder().color(TextColors.LIGHT_PURPLE).append(Text.of("Commands"))
                .build()).sendTo(src);

        return CommandResult.success();
    }

    protected ClickAction.ExecuteCallback callback(Chunk chunk) {
        return TextActions.executeCallback((commandSource -> {
            if (commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.RED + "Silly console, you can't teleport."));
                return;
            }
            Player player = (Player) commandSource;
            Location<World> a = new Location<>(chunk.getWorld(), chunk.getPosition());
            Location<World> b = new Location<>(a.getExtent(), a.getX() * 16, a.getExtent().getBlockMax().getY(), a.getZ() * 16);
            Optional<BlockRayHit<World>> c = BlockRay.from(b).stopFilter(BlockRay.onlyAirFilter()).to(a.getPosition().sub(b.getX(), 1, b.getZ()))
                    .end();

            if (c.isPresent()) {
                BlockRayHit<World> d = c.get();
                player.setLocation(d.getLocation());
            } else {
                commandSource.sendMessage(Text.of("Could not send you to: " + a.getX() + "," + a.getZ()));
            }
        }));
    }

    public List<Text> getCommands() {
        List<Text> texts = new ArrayList<>();
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/lc tiles")).onHover(TextActions.showText(Text.of("Search for chunks with "
                + "lots of tiles."))).append(Text.of("/lc tiles")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/lc entities")).onHover(TextActions.showText(Text.of("Search for chunks with "
                + "lots of entities."))).append(Text.of("/lc entities")).build());
        return texts;
    }
}
