## Messages
All config settings in messages.conf use the standard minecraft formatting codes except for **Message Color**.


### Clearing Items Message
This is the message displayed **after** after a clear has run.
%d is the variable for the number of items.

### Message Color
Message Color only affects messages not defined in messages.conf.
You can use any of the **colors** on [this page](https://jd.spongepowered.org/7.1.0/org/spongepowered/api/text/format/TextColors.html).
Make sure to check your spelling.

### Prefix
This is the part that comes before any messages from the plugin in-game.
If you would like a space between the prefix and the message, add a space just before the end quote.

### Warning Message
This setting is used when there is a minute or less until the clear.
%d is the variable for seconds.

### Warning Message mins
This setting is used when there is more than a minute until the clear.
The first %d is for minutes.
The second %d is for seconds.
