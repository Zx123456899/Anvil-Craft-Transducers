package dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism;

/**
 * 接口注入 - 为BasicEnergyContainer添加方法
 */
public interface IMekPowerManager {
    /**
     * @return 设备发电量
     */
    int getOutputPower();

    /**
     * @return 设备用电量
     */
    int getInputPower();

    /**
     * 设置设备用电量
     */
    void setInputPower(int inputPower);

    /**
     * 标记电量获取改变
     */
    void markPowerChange();
}