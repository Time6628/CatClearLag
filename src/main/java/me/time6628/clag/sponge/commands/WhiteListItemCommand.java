package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by TimeTheCat on 1/5/2017.
 */
public class WhiteListItemCommand implements CommandExecutor {
    CatClearLag plugin = CatClearLag.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) return CommandResult.empty();
        Optional<ItemType> otype = args.getOne(Text.of("item"));
        if (otype.isPresent()) {
            plugin.addIDToWhiteList(otype.get().getId());
            //TODO: replace with TextTemplate
            src.sendMessage(Text.builder()
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of("Added "))
                    .color(TextColors.GREEN)
                    .append(Text.of(otype.get().getId() + " "))
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of(" to the ClearLag whitelist."))
                    .build());
        } else if (((Player) src).getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
            ItemStack si = ((Player) src).getItemInHand(HandTypes.MAIN_HAND).get();
            plugin.addIDToWhiteList(plugin.getItemID(si));
            //TODO: replace with TextTemplate
            src.sendMessage(Text.builder()
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of("Added "))
                    .color(TextColors.GREEN)
                    .append(Text.of(si + " "))
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of(" to the ClearLag whitelist."))
                    .build());
        } else {
            src.sendMessage(Text.of(TextColors.RED, "Could not add item to whitelist."));
        }
        return CommandResult.success();
    }
}
