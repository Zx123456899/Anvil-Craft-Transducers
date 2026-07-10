package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static mekanism.common.tile.TileEntityTeleporter.TeleporterStatus;

@Mixin(TileEntityTeleporter.class)
public abstract class TileEntityTeleporterMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    public TeleporterStatus status;
    @Shadow
    private MachineEnergyContainer<TileEntityTeleporter> energyContainer;
    @Unique
    private PowerGrid grid = null;

    public TileEntityTeleporterMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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

    @Redirect(method = "onUpdateServer", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityTeleporter;shouldRender:Z", opcode = Opcodes.PUTFIELD))
    public void anvilCraftTransducers$setShouldRender(TileEntityTeleporter instance, boolean value) {
        instance.shouldRender = grid != null && (status == TeleporterStatus.NOT_ENOUGH_ENERGY || status == TeleporterStatus.READY) ? grid.isWorking() : value;
    }
}