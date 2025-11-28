package dev.anvilcraft.anvilcrafttransducers.mixin;


import dev.anvilcraft.anvilcrafttransducers.api.mekanism.IMekPowerConsumer;
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

@Mixin(TileEntityFactory.class)
public abstract class TileEntityFactoryMixin<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IPowerConsumer {
    @Shadow
    protected FactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors;
    @Unique
    private PowerGrid grid = null;

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
        int inputPower = 0;
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            if (
                    lookupMonitor.getCachedRecipe(i) instanceof IMekPowerConsumer mekPowerConsumer
                            && !lookupMonitor.hasNoRecipe(i)
            ) {
                inputPower += mekPowerConsumer.getInputPower();
            }
        }
        return inputPower;
    }
}
