package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.ICachedRecipe;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IOriginalBehavior;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeCacheLookupMonitor.class)
public abstract class RecipeCacheLookupMonitorMixin<RECIPE extends MekanismRecipe<?>> {
    @Shadow
    protected CachedRecipe<RECIPE> cachedRecipe;
    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @Inject(method = "updateAndProcess()Z", at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;process()V"), cancellable = true)
    public void anvilCraftTransducers$updateAndProcess(CallbackInfoReturnable<Boolean> cir) {
        if (handler instanceof IOriginalBehavior) return;
        if (handler instanceof IPowerConsumer powerConsumer
                && powerConsumer.isGridWorking()
                && powerConsumer.getGrid() instanceof IPowerGrid powerGrid
                && !powerGrid.canChange()
                && powerConsumer.getGrid().getGenerate() > 0) {
            cachedRecipe.unpauseErrors();
            cachedRecipe.process();
        } else if (handler instanceof IPowerConsumer powerConsumer
                && !powerConsumer.isGridWorking()
                && cachedRecipe instanceof ICachedRecipe cachedRecipe1) {
            cachedRecipe1.setNoEnergyError();
        } else if (cachedRecipe instanceof ICachedRecipe cachedRecipe1) {
            cachedRecipe1.setIdle();
        }
        cir.setReturnValue(true);
    }
}