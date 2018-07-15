package me.time6628.clag.sponge.commands.subcommands.removeentities;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 10/22/2016.
 */
public class RemoveAllCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage("Removing all entities...")).build());
        int affectedEnts = plugin.removeAll();
        src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage(affectedEnts + " entities removed.")).build());
        return CommandResult.affectedEntities(affectedEnts);
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand())
                .build();
    }
}
