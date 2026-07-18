package dev.anvilcraft.anvilcrafttransducers;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import dev.anvilcraft.anvilcrafttransducers.data.ModDatagen;
//import dev.anvilcraft.anvilcrafttransducers.init.AddonBlocks;
//import dev.anvilcraft.anvilcrafttransducers.init.AddonItemGroups;
//import dev.anvilcraft.anvilcrafttransducers.init.AddonItems;
import dev.anvilcraft.lib.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(AnvilCraftTransducers.MOD_ID)
public class AnvilCraftTransducers {
    public static final String MOD_ID = "anvilcrafttransducers";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AddonConfig CONFIG;
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public AnvilCraftTransducers(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        CONFIG = ConfigManager.register(MOD_ID, AddonConfig::new);
        AddonConfig.INSTANCE = CONFIG;
//        AddonItemGroups.register(modEventBus);
//        AddonBlocks.register();
//        AddonItems.register();
        ModDatagen.init();
    }

    public static @NotNull ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
