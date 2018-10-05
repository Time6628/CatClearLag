package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 * Created by TimeTheCat on 6/26/2017.
 */
public class UnloadChunksCommand implements CommandExecutor {

    private final CatClearLag plugin = CatClearLag.instance;

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Force all chunks to unload."))
                .permission("catclearlag.command.unloadchunks")
                .executor(new UnloadChunksCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage("Unloading all chunks...")).build());
        int chunks = plugin.unloadChunks();
        src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage(chunks + " chunks unloaded.")).build());
        return CommandResult.successCount(chunks);
    }
}
