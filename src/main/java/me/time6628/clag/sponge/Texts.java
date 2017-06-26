package me.time6628.clag.sponge;

import static org.spongepowered.api.text.TextTemplate.arg;

import jdk.nashorn.internal.objects.annotations.Setter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by TimeTheCat on 4/13/2017.
 */
public class Texts {
    private static TextColor messageColor = TextColors.LIGHT_PURPLE;
    private static TextColor warningColor = TextColors.RED;
    private static TextColor secondsColor = TextColors.WHITE;
    private static Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();
    private static String stringClearMsg = "{count} items have been cleared.";

    public static Text clearMsg(int items) {
        return Text.builder()
                .append(prefix)
                .color(messageColor)
                .append(Text.of(stringClearMsg.replaceAll("\\{count}", String.valueOf(items))))
                .build();
    }


    static void setPrefix(Text prefix) {
        Texts.prefix = prefix;
    }

    public static Text getPrefix() {
        return prefix;
    }

    static void setWarningColor(TextColor warningColor) {
        Texts.warningColor = warningColor;
    }

    static void setMessageColor(TextColor messageColor) {
        Texts.messageColor = messageColor;
    }

    public static TextColor getMessageColor() {
        return messageColor;
    }

    public static TextColor getWarningColor() {
        return warningColor;
    }

    public static String getStringClearMsg() {
        return stringClearMsg;
    }

    static void setStringClearMsg(String stringClearMsg) {
        Texts.stringClearMsg = stringClearMsg;
    }

    public static TextTemplate warningMessage = TextTemplate.of(
            warningColor, "Ground items will be cleared in ",
            arg("seconds").color(secondsColor),
            warningColor, "seconds."
    );

    public static void setSecondsColor(TextColor secondsColor) {
        Texts.secondsColor = secondsColor;
    }

}
