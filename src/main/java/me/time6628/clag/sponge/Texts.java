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

    public static TextColor messageColor = TextColors.LIGHT_PURPLE;
    public static TextColor warningColor = TextColors.RED;
    public static TextColor secondsColor = TextColors.WHITE;
    public static Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();

    public static TextTemplate clearMsg = TextTemplate.of(
            prefix, warningColor,
            arg("count").color(secondsColor), " items have been cleared."
    );

    public static TextTemplate warningMessage = TextTemplate.of(
            warningColor, "Ground items will be cleared in ",
            arg("seconds").color(secondsColor),
            warningColor, " seconds."
    );

}
