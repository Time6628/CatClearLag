package me.time6628.clag.sponge;

import static org.spongepowered.api.text.TextTemplate.arg;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 4/13/2017.
 */
public class Texts {

    public static TextColor messageColor = TextColors.LIGHT_PURPLE;
    private static final TextColor warningColor = TextColors.RED;
    private static final TextColor secondsColor = TextColors.WHITE;
    private static final Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();

    public static TextTemplate clearMsg = TextTemplate.of(
            prefix, warningColor,
            arg("count").color(secondsColor), " items have been cleared."
    );

    public static final TextTemplate warningMessage = TextTemplate.of(
            prefix, warningColor, "Ground items will be cleared in ",
            arg("seconds").color(secondsColor),
            warningColor, " seconds."
    );

}
