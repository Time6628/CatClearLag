package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.Messages;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.Optional;

/**
 * Created by TimeTheCat on 1/5/2017.
 */
public class WhiteListItemCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Add an itemtype to the clearlag whitelist"))
                .permission("catclearlag.command.whitelistitem")
                .executor(new WhiteListItemCommand())
                .arguments(GenericArguments.optional(GenericArguments.catalogedElement(Text.of("item"), ItemType.class)))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) return CommandResult.empty();
        Optional<ItemType> otype = args.getOne(Text.of("item"));
        if (otype.isPresent()) {
            src.sendMessage(Messages.addToWhileList.apply(Collections.singletonMap("item", addItemToWhitelist(otype.get()))).build());
        } else if (((Player) src).getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
            ItemStack si = ((Player) src).getItemInHand(HandTypes.MAIN_HAND).get();
            src.sendMessage(Messages.addToWhileList.apply(Collections.singletonMap("item", addItemToWhitelist(si.getType()))).build());
        } else {
            src.sendMessage(Text.of(TextColors.RED, "Could not add item to whitelist."));
        }
        return CommandResult.success();
    }


    private String addItemToWhitelist(ItemType item) {
        plugin.addIDToWhiteList(item.getId());
        return item.getId();
    }
}
