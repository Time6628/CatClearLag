package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import static org.spongepowered.api.text.TextTemplate.arg;

@ConfigSerializable
public class MessagesConfig {

    @Setting("Message Color")
    public TextColor messageColor = TextColors.LIGHT_PURPLE;

    @Setting("WarningColor")
    public TextColor warningColor = TextColors.RED;

    @Setting("Seconds Color")
    public TextColor secondsColor = TextColors.WHITE;

    @Setting("Prefix")
    public Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[ClearLag] ")).build();

    @Setting("Clearing Items Message")
    public TextTemplate clearMsg = TextTemplate.of(
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
