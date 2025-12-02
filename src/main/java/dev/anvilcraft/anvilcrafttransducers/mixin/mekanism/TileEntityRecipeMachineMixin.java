package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IMekPowerConsumer;
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

/**
 * <p>
 * 为绝大多数的耗能设备实现IPowerConsumer，并入铁砧工艺的电网组件
 * </p>
 * <p>
 * 但其中有一个例外<太阳能中子活化器>，但无大碍，就不单独兼容了
 */
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
        if (
                canFunction()
                        && recipeCacheLookupMonitor.getCachedRecipe(0) instanceof IMekPowerConsumer mekPowerConsumer
                        && !recipeCacheLookupMonitor.hasNoRecipe(0)
        ) {
            return mekPowerConsumer.getInputPower();
        }
        return 0;
    }
}
