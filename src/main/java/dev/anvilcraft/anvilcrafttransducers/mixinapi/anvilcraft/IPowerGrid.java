package dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft;

/**
 * 接口注入 - 为电网添加方法
 */
public interface IPowerGrid {
    /**
     * @return 电网是否需要重载
     */
    boolean canChange();
}
