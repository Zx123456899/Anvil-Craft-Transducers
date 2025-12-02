package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IMekPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.TileEntityChargepad;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityElectricPump;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityChargepad.class)
public abstract class TileEntityChargepadMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    private MachineEnergyContainer<TileEntityElectricPump> energyContainer;
    @Unique
    private PowerGrid grid = null;

    public TileEntityChargepadMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
        ((IMekPowerManager) energyContainer).markPowerChange();
        this.grid = grid;
    }

    @Override
    public int getInputPower() {
        return ((IMekPowerManager) energyContainer).getInputPower();
    }

    @Inject(
            method = "onUpdateServer",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$onUpdateServer(CallbackInfoReturnable<Boolean> cir) {
        if (!getActive()) {
            ((IMekPowerManager) energyContainer).setInputPower(0);
        }
    }
}
