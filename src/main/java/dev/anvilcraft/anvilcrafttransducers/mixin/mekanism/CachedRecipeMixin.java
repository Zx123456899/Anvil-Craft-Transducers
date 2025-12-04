package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ICachedRecipe;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IMekPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

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

    @Shadow
    protected abstract void updateErrors(Set<CachedRecipe.OperationTracker.RecipeError> errors);

    @Shadow
    public abstract boolean isInputValid();

    @Override
    public int getInputPower() {
        return (int) perTickEnergy.getAsLong() / AnvilCraftTransducers.CONFIG.transducers;
    }

    @Override
    public void setNoEnergyError() {
        updateErrors(Set.of(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY));
        setActive.accept(false);
        resetNoRecipeProcess();
    }

    @Override
    public void setIdle() {
        updateErrors(Collections.emptySet());
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
     * {@link #useEnergy}的实现逻辑由{@link PowerGrid#flush}通过{@link #getInputPower}自动计算
     * </p>
     */
    @Inject(
            method = "setEnergyRequirements",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$setEnergyRequirements(LongSupplier perTickEnergy, IEnergyContainer energyContainer, CallbackInfoReturnable<CachedRecipe<RECIPE>> cir) {
        if (
                energyContainer instanceof MachineEnergyContainerAccessor<?> machineEnergyContainerAccessor
                        && machineEnergyContainerAccessor.getTile() instanceof IPowerConsumer powerConsumer
                        && powerConsumer.getGrid() != null
        ) {
            this.storedEnergy = () -> Long.MAX_VALUE;
            this.useEnergy = energy -> {
            };
        }
    }
}
