package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import static org.spongepowered.api.text.TextTemplate.arg;

@ConfigSerializable
public class MessagesConfig {

    @Setting("Message Color")
    public final TextColor messageColor = TextColors.LIGHT_PURPLE;

    @Setting("WarningColor")
    private final TextColor warningColor = TextColors.RED;

    @Setting("Seconds Color")
    private final TextColor secondsColor = TextColors.WHITE;

    @Setting("Prefix")
    public final Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();

    @Setting("Clearing Items Message")
    public final TextTemplate clearMsg = TextTemplate.of(
            prefix, warningColor,
            arg("count").color(secondsColor), " items have been cleared."
    );

    @Setting("Warning Message")
    public TextTemplate warningMessage = TextTemplate.of(
            warningColor, "Ground items will be cleared in ",
            arg("seconds").color(secondsColor),
            warningColor, " seconds."
    );
}
