package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IOriginalBehavior;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ILaserEnergyContainer;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ITileHoldingEnergyContainer;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.util.PowerConversionUtil;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
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
public abstract class BasicEnergyContainerMixin implements IEnergyContainer, IExternalPowerManager, ITileHoldingEnergyContainer {
    @Unique
    private int outputPower = 0;
    @Unique
    private int inputPower = 0;
    @Unique
    private boolean changePower = false;
    /**
     * 用于非 MachineEnergyContainer 的 BasicEnergyContainer（如 TileEntityMoreMachineGenerator 中的能量容器）
     * 存储所属的 TileEntity 引用，使 BasicEnergyContainerMixin 能够获取到对应的 PowerGrid。
     */
    @Unique
    private TileEntityMekanism act$tile = null;
    @Shadow
    private long stored;

    /**
     * 设置所属的 TileEntity（用于非 MachineEnergyContainer 的 BasicEnergyContainer）
     */
    @Unique
    public void anvilCraftTransducers$setTile(TileEntityMekanism tile) {
        this.act$tile = tile;
    }

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
        return act$tile;
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

    @Inject(method = "getEnergy", at = @At("RETURN"), cancellable = true)
    public void anvilCraftTransducers$getEnergy(CallbackInfoReturnable<Long> cir) {
        if (isOriginalBehavior()) return;
        TileEntityMekanism machine = getMachine();
        // 发电机：返回0，让发电机认为容器是空的，从而持续发电
        if (machine instanceof IPowerProducer) {
            cir.setReturnValue(0L);
            return;
        }
        // 消耗器：返回电网总发电量，让消耗器认为有足够能量运行
        PowerGrid grid = getGrid();
        if (grid != null) {
            cir.setReturnValue(PowerConversionUtil.toEnergy(grid.getGenerate(), "mekanism"));
        } else {
            cir.setReturnValue(0L);
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
        // 发电机：总是需要能量（让发电机持续发电）
        if (getMachine() instanceof IPowerProducer) {
            return Long.MAX_VALUE;
        }
        // 消耗器：不需要存储能量（从电网实时获取）
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
        changePower = inputPower > 0;
        if (action.execute()) {
            onContentsChanged();
        }
        PowerGrid grid = getGrid();
        if (grid != null && grid.isWorking() && !((IPowerGrid) grid).canChange() && (changePower || grid.getRemaining() >= inputPower)) {
            cir.setReturnValue(amount);
        } else {
            cir.setReturnValue(0L);
        }
    }
}