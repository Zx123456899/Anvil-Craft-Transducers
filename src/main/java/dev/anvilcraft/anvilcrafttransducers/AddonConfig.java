package dev.anvilcraft.anvilcrafttransducers;

import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;

@Config(name = AnvilCraftTransducers.MOD_ID)
public class AddonConfig {
    @Comment("1kW = ?MJ")
    public int transducers = 10;
}
