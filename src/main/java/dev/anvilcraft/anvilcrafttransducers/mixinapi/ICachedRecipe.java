package dev.anvilcraft.anvilcrafttransducers.mixinapi;

public interface ICachedRecipe extends IExternalPowerConsumer {
    void setNoEnergyError();

    void setIdle();

    void resetNoRecipeProcess();
}