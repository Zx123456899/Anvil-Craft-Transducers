package dev.anvilcraft.anvilcrafttransducers.api.mekanism;

/**
 * 接口注入 - 为Mek用电设备添加方法
 */
public interface IMekPowerConsumer {
    /**
     * @return 设备用电量
     */
    int getInputPower();
}
