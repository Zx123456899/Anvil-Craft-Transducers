package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ICachedRecipe;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IOriginalBehavior;
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

    /**
     * 控制配方的执行
     */
    @Inject(
            method = "updateAndProcess()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/api/recipes/cache/CachedRecipe;process()V"
            ),
            cancellable = true
    )
    public void anvilCraftTransducers$updateAndProcess(CallbackInfoReturnable<Boolean> cir) {
        // 当设备实现IOriginalBehavior时跳出，使用原本逻辑
        if (handler instanceof IOriginalBehavior) return;
        if (
            // 当电网未过载且电网不需要变动时，执行配方
                handler instanceof IPowerConsumer powerConsumer
                        && powerConsumer.isGridWorking()
                        && powerConsumer.getGrid() instanceof IPowerGrid powerGrid
                        && !powerGrid.canChange()
                        // 由于电网在发电量和用电量为0的情况下也会工作，所以额外检测发电量
                        && powerConsumer.getGrid().getGenerate() > 0
        ) {
            cachedRecipe.unpauseErrors();
            cachedRecipe.process();
        } else if (
            // 当电网过载时,设置能量不足错误
                handler instanceof IPowerConsumer powerConsumer
                        && !powerConsumer.isGridWorking()
                        && cachedRecipe instanceof ICachedRecipe cachedRecipe1
        ) {
            cachedRecipe1.setNoEnergyError();
        } else if (
            // 以上条件未通过时，设置为闲置状态
                cachedRecipe instanceof ICachedRecipe cachedRecipe1
        ) {
            cachedRecipe1.setIdle();
        }
        cir.setReturnValue(true);
    }
}
