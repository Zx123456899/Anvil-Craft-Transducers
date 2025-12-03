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
     * 不改变电量获取标记
     * @return 设备用电量
     */
    int getNoChangeInputPower();

    /**
     * 设置设备用电量
     */
    void setInputPower(int inputPower);

    /**
     * 标记电量获取改变
     */
    void markPowerChange();

    /**
     * 设置已获取过电量
     */
    void setPowerChanged();

    /**
     * @return 是否电量改变
     */
    boolean isPowerChange();
}