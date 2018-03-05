package me.time6628.clag.sponge.commands.subcommands.removeentities;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 1/30/2017.
 */
public class RemoveLivingCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage("Removing all living entities...")).build());
        int affectedEnts = plugin.removeLiving();
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage("Removed " + affectedEnts + " living entites.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Remove all living entities from the server."))
                .permission("catclearlag.command.removelving")
                .executor(new RemoveLivingCommand())
                .build();
    }
}
