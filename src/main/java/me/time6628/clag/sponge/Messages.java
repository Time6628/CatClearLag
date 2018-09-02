package me.time6628.clag.sponge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

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
}
