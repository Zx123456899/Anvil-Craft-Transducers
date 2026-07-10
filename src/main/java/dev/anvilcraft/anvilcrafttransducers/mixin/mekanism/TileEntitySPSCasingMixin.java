package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.util.PowerConversionUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntitySPSCasing.class)
public abstract class TileEntitySPSCasingMixin extends TileEntityMultiblock<SPSMultiblockData> implements IPowerConsumer {
    @Unique
    private PowerGrid grid = null;

    public TileEntitySPSCasingMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Shadow
    public abstract MultiblockManager<SPSMultiblockData> getManager();

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
    public int getInputPower() {
        if (this == getStructure().getController()
                && getMultiblockData(getManager()) instanceof SPSMultiblockData SPSData
                && SPSData.couldOperate) {
            return PowerConversionUtil.toKilowatts(MekanismConfig.general.spsEnergyPerInput.get(), "mekanism");
        }
        return 0;
    }
}