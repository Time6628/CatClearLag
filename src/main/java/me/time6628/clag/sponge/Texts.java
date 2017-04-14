package me.time6628.clag.sponge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 4/13/2017.
 */
public class Texts {
    public static TextColor messageColor;
    public static TextColor warningColor;
    public static Text prefix;
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

    public static Text clearMessage() {
        return Text.builder().append(prefix).color(messageColor).append(Text.of("All ground items have been cleared.")).build();
    }
}
