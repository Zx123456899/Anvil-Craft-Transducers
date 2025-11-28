package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.api.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.api.mekanism.ICachedRecipe;
import dev.anvilcraft.anvilcrafttransducers.api.mekanism.IMekPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Mixin(CachedRecipe.class)
public abstract class CachedRecipeMixin<RECIPE extends MekanismRecipe<?>> implements ICachedRecipe, IMekPowerConsumer {
    @Shadow
    private LongSupplier storedEnergy;
    @Shadow
    private LongConsumer useEnergy;
    @Shadow
    private LongSupplier perTickEnergy;
    @Shadow
    private BooleanConsumer setActive;
    @Shadow
    private int operatingTicks;
    @Shadow
    private IntConsumer operatingTicksChanged;
    /**
     * 电网提供者
     */
    @Unique
    private Supplier<PowerGrid> gridSupplier;

    @Shadow
    protected abstract void updateErrors(Set<CachedRecipe.OperationTracker.RecipeError> errors);

    @Shadow
    public abstract boolean isInputValid();

    @Override
    public int getInputPower() {
        return (int) perTickEnergy.getAsLong();
    }

    @Override
    public void setNoEnergyError() {
        updateErrors(Set.of(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY));
        setActive.accept(false);
        resetNoRecipeProcess();
    }

    @Override
    public void resetNoRecipeProcess() {
        if (!isInputValid()) {
            operatingTicks = 0;
            operatingTicksChanged.accept(operatingTicks);
        }
    }

    /**
     * {@link CachedRecipe#setEnergyRequirements}
     *
     * <p>
     * 修改所有缓存配方的能量需求
     * </p>
     *
     * <p>
     * 最主要是perTickEnergy的修改，此为每tick能量消耗<br>
     * </p>
     *
     * <p>
     * {@link #storedEnergy}始终返回{@link  Long#MAX_VALUE}，由{@link PowerGrid#isWorking}来控制设备的运行<br>
     * {@link #useEnergy}的实现逻辑由{@link PowerGrid#flush} 通过{@link #getInputPower}自动计算
     * </p>
     *
     * <p>
     * TODO 将转换率写入配置，目前为10:1
     * </p>
     */
    @Inject(
            method = "setEnergyRequirements",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$setEnergyRequirements(LongSupplier perTickEnergy, IEnergyContainer energyContainer, CallbackInfoReturnable<CachedRecipe<RECIPE>> cir) {
        if (
                energyContainer instanceof MachineEnergyContainer<?> machineEnergyContainer
                        && ((MachineEnergyContainerAccessor<?>) energyContainer).getTile() instanceof IPowerConsumer powerConsumer
                        && powerConsumer.getGrid() != null
        ) {
            this.perTickEnergy = () -> machineEnergyContainer.getEnergyPerTick() / 10;
            this.storedEnergy = () -> Long.MAX_VALUE;
            this.useEnergy = energy -> {
            };
            this.gridSupplier = powerConsumer::getGrid;
        }
    }

    /**
     * 检测{@link PowerGrid#isWorking}和{@link IPowerGrid#canChange}控制配方是否执行
     */
    @Inject(
            method = "process",
            at = @At("HEAD"),
            cancellable = true
    )
    public void anvilCraftTransducers$process(CallbackInfo ci) {
        // gridSupplier为null时，意味着机器并没有能量需求，所以直接跳出
        if (gridSupplier == null) return;
        PowerGrid grid = gridSupplier.get();
        if (
                grid == null
                        || !grid.isWorking()
                        || grid instanceof IPowerGrid powerGrid
                        && powerGrid.canChange()
        ) {
            ci.cancel();
        }
    }
}
