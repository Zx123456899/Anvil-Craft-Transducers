package dev.anvilcraft.anvilcrafttransducers.mixin.moremachine;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
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
import com.jerry.mekaf.common.capabilities.energy.AdvancedFactoryEnergyContainer;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;

@Mixin(TileEntityAdvancedFactoryBase.class)
public abstract class TileEntityAdvancedFactoryBaseMixin<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IPowerConsumer {
    @Shadow
    protected AdvancedFactoryEnergyContainer energyContainer;
    @Unique
    private PowerGrid grid = null;

    public TileEntityAdvancedFactoryBaseMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
        ((IExternalPowerManager) energyContainer).markPowerChange();
        this.grid = grid;
    }

    @Override
    public int getInputPower() {
        return ((IExternalPowerManager) energyContainer).getInputPower();
    }
}
