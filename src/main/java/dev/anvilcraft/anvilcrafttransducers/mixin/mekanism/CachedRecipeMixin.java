package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.ICachedRecipe;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerConsumer;
import dev.anvilcraft.anvilcrafttransducers.util.PowerConversionUtil;
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
public abstract class CachedRecipeMixin<RECIPE extends MekanismRecipe<?>> implements ICachedRecipe, IExternalPowerConsumer {
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
        return PowerConversionUtil.toKilowatts(perTickEnergy.getAsLong(), "mekanism");
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

    @Inject(method = "setEnergyRequirements", at = @At("RETURN"))
    public void anvilCraftTransducers$setEnergyRequirements(LongSupplier perTickEnergy, IEnergyContainer energyContainer, CallbackInfoReturnable<CachedRecipe<RECIPE>> cir) {
        if (energyContainer instanceof MachineEnergyContainerAccessor<?> machineEnergyContainerAccessor
                && machineEnergyContainerAccessor.getTile() instanceof IPowerConsumer powerConsumer
                && powerConsumer.getGrid() != null) {
            this.storedEnergy = () -> Long.MAX_VALUE;
            this.useEnergy = energy -> {};
        }
    }
}