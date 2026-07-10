package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityRecipeMachine.class)
public abstract class TileEntityRecipeMachineMixin<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IPowerConsumer {
    @Shadow
    protected RecipeCacheLookupMonitor<RECIPE> recipeCacheLookupMonitor;
    @Unique
    private PowerGrid grid = null;

    public TileEntityRecipeMachineMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
        if (canFunction()
                && recipeCacheLookupMonitor.getCachedRecipe(0) instanceof IExternalPowerConsumer externalPowerConsumer
                && !recipeCacheLookupMonitor.hasNoRecipe(0)) {
            return externalPowerConsumer.getInputPower();
        }
        return 0;
    }
}