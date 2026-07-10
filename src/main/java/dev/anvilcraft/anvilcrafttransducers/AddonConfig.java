package dev.anvilcraft.anvilcrafttransducers;

import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(name = AnvilCraftTransducers.MOD_ID)
public class AddonConfig {
    public static final AddonConfig INSTANCE = AnvilCraftTransducers.CONFIG;

    @Comment("Mekanism: 1kW = ?J")
    public int mekanism = 200;

    @Comment("TechReborn: 1kW = ?EU")
    public int techreborn = 100;

    @Comment("IndustrialCraft 2: 1kW = ?EU")
    public int ic2 = 100;

    @Comment("Thermal: 1kW = ?RF")
    public int thermal = 200;

    @Comment("Create: 1kW = ?RPM")
    public int create = 50;

    @Comment("Applied Energistics 2: 1kW = ?AE")
    public int ae2 = 100;

    @Comment("Immersive Engineering: 1kW = ?RF")
    public int immersiveengineering = 200;

    public int getTransducerFactor(String modId) {
        return switch (modId.toLowerCase()) {
            case "mekanism" -> mekanism;
            case "techreborn", "tr" -> techreborn;
            case "ic2", "industrialcraft2" -> ic2;
            case "thermal", "thermalexpansion", "thermalfoundation", "thermalcultivation", "thermalinnovation" -> thermal;
            case "create" -> create;
            case "ae2", "appliedenergistics2" -> ae2;
            case "immersiveengineering", "ie" -> immersiveengineering;
            default -> 200;
        };
    }
}