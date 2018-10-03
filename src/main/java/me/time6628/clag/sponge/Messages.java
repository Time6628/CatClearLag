package me.time6628.clag.sponge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import static org.spongepowered.api.text.TextTemplate.arg;

public class Messages {

    private static final CatClearLag plugin = CatClearLag.instance;

    public static Text getPrefix() {
        return TextSerializers.FORMATTING_CODE.deserialize(plugin.getMessagesCfg().prefix);
    }

    public static Text getClearMsg(int count) {
        return Text.builder()
                .append(getPrefix())
                .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().clearMsg, count)))
                .build();
    }

    public static Text getWarningMsg(int seconds) {
        return Text.builder()
                .append(getPrefix())
                .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().warningMsg, seconds)))
                .build();
    }

    public static Text colorMessage(String text) {
        return Text.builder().color(plugin.getMessagesCfg().messageColor).append(Text.of(text)).build();
    }

    public static final TextTemplate addToWhileList = TextTemplate.of(
            TextColors.LIGHT_PURPLE, "Added ",
            TextColors.WHITE, arg("item"),
            TextColors.LIGHT_PURPLE, " to the whitelist.");
}
