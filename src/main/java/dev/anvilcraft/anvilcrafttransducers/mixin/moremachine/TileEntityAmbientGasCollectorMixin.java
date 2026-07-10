package dev.anvilcraft.anvilcrafttransducers.mixin.moremachine;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector;

@Mixin(TileEntityAmbientGasCollector.class)
public class TileEntityAmbientGasCollectorMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    private MachineEnergyContainer<TileEntityAmbientGasCollector> energyContainer;
    @Unique
    private PowerGrid grid = null;

    public TileEntityAmbientGasCollectorMixin(net.minecraft.core.Holder<net.minecraft.world.level.block.Block> blockProvider, BlockPos pos, BlockState state) {
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
