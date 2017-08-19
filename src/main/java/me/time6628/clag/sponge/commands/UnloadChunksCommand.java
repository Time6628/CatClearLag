package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 6/26/2017.
 */
public class UnloadChunksCommand implements CommandExecutor {

    private final CatClearLag plugin = CatClearLag.instance;

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage("Unloading all chunks...")).build());
        int chunks = plugin.unloadChunks();
        src.sendMessage(Text.builder().append(plugin.getMessages().prefix).append(plugin.colorMessage(chunks + " chunks unloaded.")).build());
        return CommandResult.successCount(chunks);
    }
}
