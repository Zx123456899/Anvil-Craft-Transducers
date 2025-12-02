package dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism;

public interface ICachedRecipe {
    /**
     * 设置能量不足错误
     */
    void setNoEnergyError();

    /**
     * 设置闲置
     */
    void setIdle();

    /**
     * 重置无配方槽位的配方进度
     */
    void resetNoRecipeProcess();
}
