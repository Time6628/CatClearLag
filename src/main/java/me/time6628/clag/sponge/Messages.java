package me.time6628.clag.sponge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import static org.spongepowered.api.text.TextTemplate.arg;

public class Messages {

    public static final TextTemplate addToWhileList = TextTemplate.of(
            TextColors.LIGHT_PURPLE, "Added ",
            TextColors.WHITE, arg("item"),
            TextColors.LIGHT_PURPLE, " to the whitelist.");
    private static final CatClearLag plugin = CatClearLag.instance;

    public static Text getPrefix() {
        return TextSerializers.FORMATTING_CODE.deserialize(plugin.getMessagesCfg().prefix);
    }

    public static Text getClearMsg(int count) {
        return getClearMsg(count, true);
    }

    public static Text getClearMsg(int count, boolean withPrefix) {
        return withPrefix ? Text.builder()
                .append(getPrefix())
                .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().clearMsg, count)))
                .build()
                : Text.builder()
                .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().clearMsg, count)))
                .build()
                ;
    }

    public static Text getWarningMsg(int seconds) {
        return getWarningMsg(seconds, true);
    }

    public static Text getWarningMsg(int seconds, boolean withPrefix) {
        Text.Builder builder = Text.builder();
        if (withPrefix) builder.append(getPrefix());
        if (seconds >= 60 && seconds % 60 != 0)
            return builder
                    .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().warningMsgMins, seconds / 60, seconds % 60)))
                    .build();
        return builder
                .append(TextSerializers.FORMATTING_CODE.deserialize(String.format(plugin.getMessagesCfg().warningMsg, seconds)))
                .build();
    }

    public static Text colorMessage(String text) {
        return Text.builder().color(plugin.getMessagesCfg().messageColor).append(Text.of(text)).build();
    }
}
