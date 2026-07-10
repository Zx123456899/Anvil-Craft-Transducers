package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IOriginalBehavior;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.anvilcraft.anvilcrafttransducers.util.PowerConversionUtil;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public abstract class AbstractExternalEnergyContainerMixin implements IExternalPowerManager {
    @Unique
    private int outputPower = 0;
    @Unique
    private int inputPower = 0;
    @Unique
    private boolean changePower = false;
    @Unique
    private final String modId;

    protected AbstractExternalEnergyContainerMixin(String modId) {
        this.modId = modId;
    }

    protected abstract @Nullable IPowerComponent getPowerComponent();

    protected abstract void onContentsChanged();

    protected abstract long getStoredEnergy();

    protected abstract void setStoredEnergy(long energy);

    protected abstract boolean isMachineActive();

    @Override
    public int getOutputPower() {
        return outputPower;
    }

    @Override
    public int getInputPower() {
        changePower = inputPower > 0;
        return inputPower;
    }

    @Override
    public int getNoChangeInputPower() {
        return inputPower;
    }

    @Override
    public void setInputPower(int inputPower) {
        this.inputPower = inputPower;
    }

    @Override
    public void markPowerChange() {
        resetInputPower();
        changePower = false;
    }

    @Override
    public void setPowerChanged() {
        changePower = true;
    }

    @Override
    public boolean isPowerChange() {
        return changePower;
    }

    protected void resetInputPower() {
    }

    protected long anvilCraftTransducers$getEnergy() {
        if (isMachineActive() && (getPowerComponent() instanceof IOriginalBehavior)) {
            return getStoredEnergy();
        }
        PowerGrid grid = getGrid();
        if (grid != null) {
            return PowerConversionUtil.toEnergy(grid.getGenerate(), modId);
        }
        return 0;
    }

    protected boolean anvilCraftTransducers$setEnergy(long energy) {
        if (isMachineActive() && (getPowerComponent() instanceof IOriginalBehavior)) {
            setStoredEnergy(energy);
            return false;
        }
        return true;
    }

    protected long anvilCraftTransducers$insert(long amount) {
        if (isMachineActive() && (getPowerComponent() instanceof IOriginalBehavior)) {
            return 0;
        }
        outputPower = PowerConversionUtil.toKilowatts(amount, modId);
        onContentsChanged();
        return amount;
    }

    protected long anvilCraftTransducers$extract(long amount) {
        if (isMachineActive() && (getPowerComponent() instanceof IOriginalBehavior)) {
            return 0;
        }
        inputPower = PowerConversionUtil.toKilowatts(amount, modId);
        onContentsChanged();
        PowerGrid grid = getGrid();
        if (grid != null && grid.isWorking() && !grid.canChange() && (changePower || grid.getRemaining() >= inputPower)) {
            return amount;
        }
        return 0;
    }

    protected @Nullable PowerGrid getGrid() {
        IPowerComponent component = getPowerComponent();
        if (component != null) {
            return component.getGrid();
        }
        return null;
    }
}