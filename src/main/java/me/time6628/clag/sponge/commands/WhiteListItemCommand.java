package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
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
        Optional<ItemStack> is = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
        if (is.isPresent()) {
            ItemStack si = is.get();
            plugin.getLogger().info(si.getItem().getId());
            addItemIDToWhiteList(si.getItem());
            src.sendMessage(Text.builder()
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of("Added "))
                    .color(TextColors.GREEN)
                    .append(Text.of(is.get().getItem().getId() + " "))
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of(" to the ClearLag whitelist."))
                    .build());
        } else {
            src.sendMessage(Text.builder()
                    .color(TextColors.DARK_PURPLE)
                    .append(Text.of("Could not add to whitelist.. maybe try holding something?"))
                    .build());
        }
        return CommandResult.success();
    }

    public void addItemIDToWhiteList(ItemType type) {
        if (type.getId() == null) {
            plugin.getLogger().info("null itemtype");
        return;
    }
        try {
            List<String> aa = plugin.getWhitelistItemsAsStrings();
            aa.add(type.getId());
            plugin.getCfg().getNode("whitelist").setValue(aa);
            plugin.getCfgMgr().save(plugin.getCfg());
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getWhitelistedItems().add(type);
        plugin.getWhitelistItemsAsStrings().add(type.getId());
    }
}
