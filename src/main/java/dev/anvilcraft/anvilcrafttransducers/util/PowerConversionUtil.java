package dev.anvilcraft.anvilcrafttransducers.util;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.AddonConfig;

public class PowerConversionUtil {
    private static AddonConfig config;

    private static AddonConfig getConfig() {
        if (config == null) {
            config = AnvilCraftTransducers.CONFIG;
        }
        return config;
    }

    public static int toKilowatts(long energy, String modId) {
        int factor = getConfig().getTransducerFactor(modId);
        return (int) (energy / factor);
    }

    public static long toEnergy(int kilowatts, String modId) {
        int factor = getConfig().getTransducerFactor(modId);
        return (long) kilowatts * factor;
    }

    public static int toKilowatts(long energy) {
        return toKilowatts(energy, "mekanism");
    }

    public static long toEnergy(int kilowatts) {
        return toEnergy(kilowatts, "mekanism");
    }
}