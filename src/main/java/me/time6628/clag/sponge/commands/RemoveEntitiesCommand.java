package me.time6628.clag.sponge.commands;

import me.time6628.clag.sponge.CatClearLag;
import me.time6628.clag.sponge.EntityRemover;
import me.time6628.clag.sponge.Messages;
import me.time6628.clag.sponge.api.Type;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by TimeTheCat on 1/30/2017.
 */
public class RemoveEntitiesCommand implements CommandExecutor {
    private final CatClearLag plugin = CatClearLag.instance;

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Remove various types of entities."))
                .permission("catclearlag.command.remove")
                .arguments(
                        GenericArguments.flags()
                                .permissionFlag("catclearlag.command.remove.hostile", "h", "-hostile")
                                .permissionFlag("catclearlag.command.remove.all", "a", "-all")
                                .permissionFlag("catclearlag.command.remove.items", "i", "-item")
                                .permissionFlag("catclearlag.command.remove.living", "l", "-living")
                                .permissionFlag("catclearlag.command.remove.xp", "x", "-xp")
                                .permissionFlag("catclearlag.command.remove.passive", "p", "-passive")
                                .permissionFlag("catclearlag.command.remove.named", "n", "-named")
                                .permissionFlag("catclearlag.command.remove.animal", "m", "-animal")
                                .buildWith(GenericArguments.none()))
                .executor(new RemoveEntitiesCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (args.hasAny("a") || args.hasAny("h") || args.hasAny("i") || args.hasAny("x") || args.hasAny("l") || args.hasAny("n") || args.hasAny("m")) {
            Predicate<Entity> pred = null;
            if (args.hasAny("a"))
                pred = combinePred(pred, Type.ALL);
            if (args.hasAny("h"))
                pred = combinePred(pred, Type.HOSTILE);
            if (args.hasAny("i")) {
                pred = combinePred(pred, Type.ITEM);
            }
            if (args.hasAny("x"))
                pred = combinePred(pred, Type.XP);
            if (args.hasAny("l"))
                pred = combinePred(pred, Type.LIVING);
            if (args.hasAny("m"))
                pred = combinePred(pred, Type.ANIMAL);
            if (args.hasAny("n"))
                pred = combinePred(pred, Type.NAMED);
            EntityRemover<Entity> remover = new EntityRemover<>(pred);
            src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage("Removing entities...")).build());
            int affectedEnts = remover.removeEntities();
            src.sendMessage(Text.builder().append(Messages.getPrefix()).append(Messages.colorMessage(affectedEnts + " entities removed.")).build());
            return CommandResult.affectedEntities(affectedEnts);
        } else {
            plugin.getPaginationService().builder().contents(getFlags()).title(Text.builder().color(TextColors.LIGHT_PURPLE).append(Text.of("/re Help"))
                    .build()).sendTo(src);
        }
        return CommandResult.success();
    }

    private List<Text> getFlags() {
        List<Text> texts = new ArrayList<>();
        texts.add(ez("/re -a", "Remove all entities."));
        texts.add(ez("/re -h", "Remove all hostiles."));
        texts.add(ez("/re -i", "Remove all items."));
        texts.add(ez("/re -x", "Remove all XP Orbs."));
        texts.add(ez("/re -l", "Remove all living entities."));
        texts.add(ez("/re -m", "Remove all animals."));
        texts.add(ez("/re -n", "Prevents name tagged entities from being removed."));
        return texts;
    }

    private Text ez(String cmd, String desc) {
        return Text.builder()
                .onClick(TextActions.suggestCommand(cmd))
                .append(Text.of(cmd))
                .append(Text.of(" - " + desc))
                .build();
    }

    private Predicate<Entity> combinePred(Predicate<Entity> pred, Type type) {
        if (pred == null) {
            return plugin.getCclService().getPredicate(type);
        } else return pred.or(plugin.getCclService().getPredicate(type));
    }
}