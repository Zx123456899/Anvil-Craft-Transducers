package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IOriginalBehavior;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.anvilcraft.anvilcrafttransducers.util.PowerConversionUtil;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicEnergyContainer.class)
public abstract class BasicEnergyContainerMixin implements IEnergyContainer, IExternalPowerManager {
    @Unique
    private int outputPower = 0;
    @Unique
    private int inputPower = 0;
    @Unique
    private boolean changePower = false;
    @Shadow
    private long stored;

    @Shadow
    public abstract long getEnergy();

    @Shadow
    public abstract void onContentsChanged();

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
    public void setInputPower(int inputPower) {
        this.inputPower = inputPower;
    }

    @Override
    public int getNoChangeInputPower() {
        return inputPower;
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

    private void resetInputPower() {
        TileEntityMekanism machine = getMachine();
        MachineEnergyContainer<?> machineEnergyContainer = getMachineEnergyContainer();
        if (machine != null && machineEnergyContainer != null && machine.canFunction()) {
            setInputPower(PowerConversionUtil.toKilowatts(machineEnergyContainer.getEnergyPerTick(), "mekanism"));
        } else {
            setInputPower(0);
        }
    }

    private @Nullable MachineEnergyContainer<?> getMachineEnergyContainer() {
        if ((Object) this instanceof MachineEnergyContainer<?> machineEnergyContainer) {
            return machineEnergyContainer;
        }
        return null;
    }

    private @Nullable TileEntityMekanism getMachine() {
        if (getMachineEnergyContainer() instanceof MachineEnergyContainerAccessor<?> accessor) {
            return accessor.getTile();
        }
        if (this instanceof ILaserEnergyContainer<?> laserEnergyContainer) {
            return laserEnergyContainer.getTile();
        }
        return null;
    }

    private @Nullable PowerGrid getGrid() {
        if (getMachine() instanceof IPowerComponent powerComponent) {
            return powerComponent.getGrid();
        }
        return null;
    }

    private boolean isOriginalBehavior() {
        return getMachine() instanceof IOriginalBehavior || this instanceof IOriginalBehavior;
    }

    @Inject(method = "getEnergy", at = @At("RETURN"))
    public void anvilCraftTransducers$getEnergy(CallbackInfoReturnable<Long> cir) {
        if (isOriginalBehavior()) return;
        PowerGrid grid = getGrid();
        if (grid != null) {
            stored = PowerConversionUtil.toEnergy(grid.getGenerate(), "mekanism");
        } else {
            stored = 0;
        }
    }

    @Inject(method = "setEnergy", at = @At("RETURN"), cancellable = true)
    public void anvilCraftTransducers$setEnergy(long energy, CallbackInfo ci) {
        if (isOriginalBehavior()) return;
        ci.cancel();
    }

    @Inject(method = "isEmpty", at = @At("RETURN"), cancellable = true)
    public void anvilCraftTransducers$isEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (isOriginalBehavior()) return;
        cir.setReturnValue(getEnergy() == 0);
    }

    @Override
    public @Range(from = 0L, to = 9223372036854775807L) long getNeeded() {
        if (isOriginalBehavior()) return IEnergyContainer.super.getNeeded();
        return 0;
    }

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    public void anvilCraftTransducers$insert(long amount, Action action, AutomationType automationType, CallbackInfoReturnable<Long> cir) {
        if (isOriginalBehavior()) return;
        if (action.execute()) {
            outputPower = PowerConversionUtil.toKilowatts(amount, "mekanism");
            onContentsChanged();
        }
        cir.setReturnValue(amount);
    }

    @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
    public void anvilCraftTransducers$extract(long amount, Action action, AutomationType automationType, CallbackInfoReturnable<Long> cir) {
        if (isOriginalBehavior()) return;
        inputPower = PowerConversionUtil.toKilowatts(amount, "mekanism");
        if (action.execute()) {
            onContentsChanged();
        }
        PowerGrid grid = getGrid();
        if (grid != null && grid.isWorking() && grid.canChange() && (changePower || grid.getRemaining() >= inputPower)) {
            cir.setReturnValue(amount);
        } else {
            cir.setReturnValue(0L);
        }
    }
}