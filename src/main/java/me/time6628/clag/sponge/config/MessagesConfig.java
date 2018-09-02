package me.time6628.clag.sponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

@ConfigSerializable
public class MessagesConfig {

    @Setting("Message Color")
    public TextColor messageColor = TextColors.LIGHT_PURPLE;

    @Setting("Prefix")
    public String prefix = "&5[CatClearLag] ";

    @Setting("Clearing Items Message")
    public String clearMsg = "&f%d items have been cleared.";

    @Setting("Warning Message")
    public String warningMsg = "&cGround items will be cleared in &f%d seconds.";
}
