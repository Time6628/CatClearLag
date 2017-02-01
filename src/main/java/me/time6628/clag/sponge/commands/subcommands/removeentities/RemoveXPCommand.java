package me.time6628.clag.sponge.commands.subcommands.removeentities;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 1/31/2017.
 */
public class RemoveXPCommand implements CommandExecutor {
    CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder().append(plugin.getPrefix()).append(plugin.colorMessage("Removing all XP Orbs...")).build());
        int affectedEnts = plugin.removeXP();
        src.sendMessage(Text.builder().append(plugin.getPrefix()).append(plugin.colorMessage("Removed " + affectedEnts + " living entites.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }
}
