package me.time6628.clag.sponge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by TimeTheCat on 4/13/2017.
 */
public class Texts {
    public static TextColor messageColor = TextColors.LIGHT_PURPLE;
    public static TextColor warningColor = TextColors.RED;
    public static Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();
    public static Text clearMsg = Text.builder().append(prefix).color(messageColor).append(Text.of("All ground items have been cleared.")).build();


    public static void setPrefix(Text prefix) {
        Texts.prefix = prefix;
    }

    public static Text getPrefix() {
        return prefix;
    }

    public static void setWarningColor(TextColor warningColor) {
        Texts.warningColor = warningColor;
    }

    public static void setMessageColor(TextColor messageColor) {
        Texts.messageColor = messageColor;
    }

    public static Text warningMessage(int seconds) {
        return Text.builder().append(prefix).color(warningColor).append(Text.of("Ground items will be cleared in "))
                .append(Text.of(TextColors.WHITE, seconds))
                .append(Text.of(" seconds."))
                .build();
    }
}
