package dev.anvilcraft.anvilcrafttransducers.api.mekanism;

/**
 * 接口注入 - 为机器的缓存配方添加方法
 */
public interface ICachedRecipe {
    /**
     * 设置能量不足错误
     */
    void setNoEnergyError();

    /**
     * 重置无配方槽位的配方进度
     */
    void resetNoRecipeProcess();
}
