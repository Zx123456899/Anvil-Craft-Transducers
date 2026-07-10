package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(TileEntityFusionReactorBlock.class)
public abstract class TileEntityFusionReactorBlockMixin extends TileEntityMultiblock<FusionReactorMultiblockData> implements IPowerProducer {
    @Unique
    private PowerGrid grid = null;

    public TileEntityFusionReactorBlockMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Shadow
    public abstract MultiblockManager<FusionReactorMultiblockData> getManager();

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