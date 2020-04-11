package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by TimeTheCat on 10/28/2016.
 */
public class CLDebugCommand implements CommandExecutor {

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Debug entities."))
                .permission("catclearlag.command.cldebug")
                .executor(new CLDebugCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) throw new CommandException(Text.of("You must be a player to run this command"));
        Player player = (Player) src;
        Collection<Entity> nearbyEntities = player.getNearbyEntities(10);
        List<Text> entityText = new ArrayList<>();
        for (Entity nearbyEntity : nearbyEntities) {
            entityText.add(createCallback(nearbyEntity));
        }
        CatClearLag.instance.getPaginationService().builder().contents(entityText).title(Text.of("Entities nearby")).sendTo(src);
        return CommandResult.success();
    }

    public Text createCallback(Entity entity) {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(entity.getType().getId()));
        PaginationList keys = CatClearLag.instance.getPaginationService().builder().title(Text.of("Keys")).contents(entity.getValues().stream().map(value -> Text.of(value.getKey().getId(), " ", TextColors.GREEN, value.get(), " ", TextColors.BLUE, value.get())).collect(Collectors.toSet())).build();
        builder.onClick(TextActions.executeCallback(keys::sendTo));
        return builder.build();
    }
}
