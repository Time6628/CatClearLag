package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 10/22/2016.
 */
public class RemoveAllCommand implements CommandExecutor {
    private CatClearLag plugin;
    public RemoveAllCommand(CatClearLag catClearLag) {
        this.plugin = catClearLag;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder().append(plugin.prefix).color(TextColors.LIGHT_PURPLE).append(Text.of("Removing all entities...")).build());
        plugin.removeAll();
        src.sendMessage(Text.builder().append(plugin.prefix).color(TextColors.LIGHT_PURPLE).append(Text.of("All entities removed.")).build());
        return CommandResult.success();
    }
}
