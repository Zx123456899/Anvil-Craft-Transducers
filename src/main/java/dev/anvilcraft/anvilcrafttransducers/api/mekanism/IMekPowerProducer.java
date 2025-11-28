package dev.anvilcraft.anvilcrafttransducers.api.mekanism;

/**
 * 接口注入 - 为BasicEnergyContainer添加方法
 */
public interface IMekPowerProducer {
    /**
     * @return 设备发电量
     */
    int getOutputPower();
}