package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.api.mekanism.IMekPowerProducer;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
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
        if (!getEnergyContainers(null).isEmpty() && !getMultiblockData(getManager()).getValveData().isEmpty()) {
            // 由于电网的特性，每个端口都会获取一次发电量
            // 所以将发电量除以端口数量才能获取正常的发电量
            return ((IMekPowerProducer) getEnergyContainers(null).getFirst()).getOutputPower() / getMultiblockData(getManager()).getValveData().size();
        }
        return 0;
    }
}
