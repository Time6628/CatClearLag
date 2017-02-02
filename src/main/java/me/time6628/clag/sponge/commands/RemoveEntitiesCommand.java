package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TimeTheCat on 1/30/2017.
 */
public class RemoveEntitiesCommand implements CommandExecutor {
    CatClearLag pl = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        pl.getPaginationService().builder().contents(getCommands()).title(Text.builder().color(TextColors.LIGHT_PURPLE).append(Text.of("Commands")).build()).sendTo(src);
        return CommandResult.success();
    }

    private List<Text> getCommands() {
        List<Text> texts = new ArrayList<>();
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/re all")).onHover(TextActions.showText(Text.of("Remove all entities."))).append(Text.of("/re all")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/re hostiles")).onHover(TextActions.showText(Text.of("Remove all hostiles."))).append(Text.of("/re hostiles")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/re items")).onHover(TextActions.showText(Text.of("Remove all items."))).append(Text.of("/re items")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/re xp")).onHover(TextActions.showText(Text.of("Remove all XP Orbs."))).append(Text.of("/re xp")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/re living")).onHover(TextActions.showText(Text.of("Remove all Living entities."))).append(Text.of("/re living")).build());
        return texts;
    }
}
