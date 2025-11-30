package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.api.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.api.mekanism.ICachedRecipe;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.machine.TileEntitySolarNeutronActivator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeCacheLookupMonitor.class)
public class RecipeCacheLookupMonitorMixin<RECIPE extends MekanismRecipe<?>> {
    @Shadow
    protected CachedRecipe<RECIPE> cachedRecipe;
    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    /**
     * <p>
     * 在{@link RecipeCacheLookupMonitor#onContentsChanged}触发时，为电网标记更改{@link PowerGrid#markChanged}
     * </p>
     *
     * <p>
     * 这代表配方变动，需要重新计算电力消耗
     * </p>
     */
    @Inject(
            method = "onChange",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$onChange(CallbackInfo ci) {
        if (
                handler instanceof IPowerConsumer powerConsumer
                        && powerConsumer.getGrid() != null
        ) {
            powerConsumer.getGrid().markChanged();
        }
    }

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
        if (
            // 当设备是太阳能中子活化仪时，使用原本逻辑
            //因为它并不是用电设备
                handler instanceof TileEntitySolarNeutronActivator
        ) {
            cachedRecipe.process();
        } else if (
            // 当电网未过载且电网不需要变动时，执行配方
                handler instanceof IPowerConsumer powerConsumer
                        && powerConsumer.isGridWorking()
                        && powerConsumer.getGrid() instanceof IPowerGrid powerGrid
                        && !powerGrid.canChange()
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
            // 以上条件未通过时，重置无配方槽位的进度
            // 因为电网的更新是20Tick一次，为了视觉效果合理，提前重置无配方槽位进度
                cachedRecipe instanceof ICachedRecipe cachedRecipe1
        ) {
            cachedRecipe1.resetNoRecipeProcess();
        }
        cir.setReturnValue(true);
    }
}
