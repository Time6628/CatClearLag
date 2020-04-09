## catclearlag.conf

### Boss Bar
#### Boss Bar Color
Colors should have the `minecraft:` prefix.
Available colors are blue, green, pink, purple, red, white, and yellow.

#### Hide Boss Bar After
This should be the number of seconds after the clear the boss bar should stay on screen.

### Entity Whitelist
This is **NOT** for items. That is just the whitelist setting.
Entities from minecraft should have the prefix `minecraft:`
Entities from mods should use their modname followed by a colon as the prefix.

Whitelisted entities need to be the id names. Number ids are deprecated.
If you need multiple objects whitelisted, add a comma after the first entity's end quote then put the next entity in quotes.
For example:
"Entity Whitelist"=[
	"minecraft:boat",
	"minecraft:armor_stand",
	"mymod:entity"
]

### Interval
Interval is in **minutes**.
This is how often, in minutes, items will be cleared.

### Limits
#### Entity Check Interval
How often, in minutes, to check if there are more entities than the threshold.

#### Hostile Limit
Maximum number of hostile mobs loaded at once.

#### Max Mobs Per Chunk 
Maximum number of mobs allowed per chunk.

#### Per Chunk Limit Enabled
Whether or not to enable enforcement of Max Mobs Per Chunk.
True to enable enforcement.

#### XP Orb Limit
Maximum number of XP orbs to be loaded at once.

### Live Time
#### Min Item Live Time
Number of **seconds** an item should be on the ground before it is allowed to be cleared.

#### enabled (Live Time)
Whether or not to enable live time.

This is usually disabled in order to stop the ChangeInventoryEvent$Pickup$Pre$Impl error.

### Warnings
Number of seconds since the last clear to display warnings. 
540 would be 60 seconds if the interval were 10.
570 would be 30 seconds at interval 10.

The general formula to calcute the warnings:
Interval * 60 - <seconds before a clear>

### Whitelist
This is the same as entity whitelist except instead of entities, this is for items.


