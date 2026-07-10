package dev.anvilcraft.anvilcrafttransducers.util;

import dev.anvilcraft.anvilcrafttransducers.AddonConfig;

public class PowerConversionUtil {
    private static final AddonConfig CONFIG = AddonConfig.INSTANCE;

    public static int toKilowatts(long energy, String modId) {
        int factor = CONFIG.getTransducerFactor(modId);
        return (int) (energy / factor);
    }

    public static long toEnergy(int kilowatts, String modId) {
        int factor = CONFIG.getTransducerFactor(modId);
        return (long) kilowatts * factor;
    }

    public static int toKilowatts(long energy) {
        return toKilowatts(energy, "mekanism");
    }

    public static long toEnergy(int kilowatts) {
        return toEnergy(kilowatts, "mekanism");
    }
}