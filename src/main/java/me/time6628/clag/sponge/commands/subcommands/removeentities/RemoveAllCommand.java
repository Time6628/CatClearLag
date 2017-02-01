package me.time6628.clag.sponge.commands.subcommands.removeentities;

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
    private CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder().append(plugin.getPrefix()).append(plugin.colorMessage("Removing all entities...")).build());
        int affectedEnts = plugin.removeAll();
        src.sendMessage(Text.builder().append(plugin.getPrefix()).append(plugin.colorMessage(affectedEnts + " entities removed.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }
}
