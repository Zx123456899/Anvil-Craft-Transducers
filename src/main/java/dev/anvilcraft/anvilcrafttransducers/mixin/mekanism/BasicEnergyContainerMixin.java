package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ILaserEnergyContainer;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IMekPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicEnergyContainer.class)
public abstract class BasicEnergyContainerMixin implements IEnergyContainer, IMekPowerManager {
    /**
     * 发电量缓存
     */
    @Unique
    private int outputPower = 0;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;
    @Unique
    private boolean changePower = false;

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

    /**
     * 设备标记电力获取改变 - 只有用电设备需要
     */
    @Override
    public void markPowerChange() {
        resetInputPower();
        changePower = false;
    }

    /**
     * 重置用电缓存 - 设备并入电网后jade显示的视觉兼容
     */
    private void resetInputPower() {
        TileEntityMekanism machine = getMachine();
        MachineEnergyContainer<?> machineEnergyContainer = getMachineEnergyContainer();
        if (
                machine != null
                        && machineEnergyContainer != null
                        && machine.canFunction()
        ) {
            setInputPower((int) (machineEnergyContainer.getEnergyPerTick() / AnvilCraftTransducers.CONFIG.transducers));
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

    @Inject(
            method = "getEnergy",
            at = @At("RETURN"),
            cancellable = true
    )
    public void anvilCraftTransducers$getEnergy(CallbackInfoReturnable<Long> cir) {
        PowerGrid grid = getGrid();
        if (grid != null && grid.isWorking()) {
            cir.setReturnValue((long) grid.getGenerate() * AnvilCraftTransducers.CONFIG.transducers);
        } else {
            cir.setReturnValue(0L);
        }
    }

    @Inject(
            method = "setEnergy",
            at = @At("RETURN"),
            cancellable = true
    )
    public void anvilCraftTransducers$setEnergy(long energy, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(
            method = "isEmpty",
            at = @At("RETURN"),
            cancellable = true
    )
    public void anvilCraftTransducers$isEmpty(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(getEnergy() == 0);
    }

    /**
     * 拦截了电量输入
     */
    @Inject(
            method = "insert",
            at = @At("HEAD"),
            cancellable = true
    )
    public void anvilCraftTransducers$insert(long amount, Action action, AutomationType automationType, CallbackInfoReturnable<Long> cir) {
        if (action.execute()) {
            outputPower = (int) (amount / AnvilCraftTransducers.CONFIG.transducers);
            onContentsChanged();
        }
        if (
                !(getMachine() instanceof TileEntityLaserTractorBeam)
                        && !(getMachine() instanceof TileEntityLaserAmplifier)
        ) {
            cir.setReturnValue(amount);
        }
    }

    /**
     * 拦截了电量输出
     */
    @Inject(
            method = "extract",
            at = @At("HEAD"),
            cancellable = true
    )
    public void anvilCraftTransducers$extract(long amount, Action action, AutomationType automationType, CallbackInfoReturnable<Long> cir) {
        if (action.execute()) {
            inputPower = (int) (amount / AnvilCraftTransducers.CONFIG.transducers);
            onContentsChanged();
        }
        PowerGrid grid = getGrid();
        if (
                grid != null
                        && grid.isWorking()
                        && grid instanceof IPowerGrid powerGrid
                        && !powerGrid.canChange()
                        && (changePower || grid.getRemaining() >= inputPower)
        ) {
            cir.setReturnValue(amount);
        } else if (
                grid != null
                        && !(getMachine() instanceof TileEntityLaserTractorBeam)
                        && !(getMachine() instanceof TileEntityLaserAmplifier)
        ) {
            cir.setReturnValue(0L);
        }
    }
}
