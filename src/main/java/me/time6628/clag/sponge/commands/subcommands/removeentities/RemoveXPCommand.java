package me.time6628.clag.sponge.commands.subcommands.removeentities;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 1/31/2017.
 */
public class RemoveXPCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage("Removing all XP Orbs...")).build());
        int affectedEnts = plugin.removeXP();
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage("Removed " + affectedEnts + " living entites.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Remove all XP Orbs from the server."))
                .permission("catclearlag.command.removexp")
                .executor(new RemoveXPCommand())
                .build();
    }
}
