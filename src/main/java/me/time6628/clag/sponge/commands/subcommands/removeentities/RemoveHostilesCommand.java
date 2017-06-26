package me.time6628.clag.sponge.commands.subcommands.removeentities;

import com.google.inject.Inject;
import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Texts;
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
public class RemoveHostilesCommand implements CommandExecutor {
    private CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder().append(Texts.getPrefix()).append(plugin.colorMessage("Removing hostiles...")).build());
        int affectedEnts = plugin.removeHostile();
        src.sendMessage(Text.builder().append(Texts.getPrefix()).append(plugin.colorMessage(affectedEnts + " hostiles removed.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }
}
