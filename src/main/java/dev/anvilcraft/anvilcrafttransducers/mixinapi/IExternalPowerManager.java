package dev.anvilcraft.anvilcrafttransducers.mixinapi;

public interface IExternalPowerManager {
    int getOutputPower();

    int getInputPower();

    int getNoChangeInputPower();

    void setInputPower(int inputPower);

    void markPowerChange();

    void setPowerChanged();

    boolean isPowerChange();
}