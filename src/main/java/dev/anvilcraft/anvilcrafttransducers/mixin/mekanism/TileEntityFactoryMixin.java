package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityFactory.class)
public abstract class TileEntityFactoryMixin<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IPowerConsumer {
    @Shadow
    protected FactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors;
    @Unique
    private PowerGrid grid = null;
    @Unique
    private int activeCount = 0;

    public TileEntityFactoryMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public int getInputPower() {
        if (!canFunction()) return 0;
        int inputPower = 0;
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            if (lookupMonitor.getCachedRecipe(i) instanceof IExternalPowerConsumer externalPowerConsumer
                    && !lookupMonitor.hasNoRecipe(i)) {
                inputPower += externalPowerConsumer.getInputPower();
            }
        }
        return inputPower;
    }

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/factory/TileEntityFactory;setActive(Z)V"))
    public void anvilCraftTransducers$onUpdateServer(CallbackInfoReturnable<Boolean> cir) {
        if (!canFunction()) return;
        int lastActiveCount = activeCount;
        activeCount = 0;
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            if (!recipeCacheLookupMonitors[i].hasNoRecipe(i)) {
                activeCount++;
            }
        }
        if (activeCount > lastActiveCount && grid != null) {
            grid.markChanged();
        }
    }
}