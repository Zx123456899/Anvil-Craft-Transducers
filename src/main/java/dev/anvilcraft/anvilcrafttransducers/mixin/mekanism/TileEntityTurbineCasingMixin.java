package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(TileEntityTurbineCasing.class)
public abstract class TileEntityTurbineCasingMixin extends TileEntityMultiblock<TurbineMultiblockData> implements IPowerProducer {
    @Unique
    private PowerGrid grid = null;

    public TileEntityTurbineCasingMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
    public int getRange() {
        return 1;
    }

    @Override
    public int getOutputPower() {
        List<IEnergyContainer> energyContainers = getMultiblock().getEnergyContainers(null);
        if (this == getStructure().getController() && !energyContainers.isEmpty()) {
            return ((IExternalPowerManager) energyContainers.getFirst()).getOutputPower();
        }
        return 0;
    }
}