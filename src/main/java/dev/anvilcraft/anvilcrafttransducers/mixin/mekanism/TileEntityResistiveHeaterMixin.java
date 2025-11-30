package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.util.MekPowerConsumerUtils;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.capabilities.energy.ResistiveHeaterEnergyContainer;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityResistiveHeater;
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

@Mixin(TileEntityResistiveHeater.class)
public abstract class TileEntityResistiveHeaterMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    private ResistiveHeaterEnergyContainer energyContainer;
    @Shadow
    private BasicHeatCapacitor heatCapacitor;
    @Unique
    private PowerGrid grid = null;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;

    public TileEntityResistiveHeaterMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
        return inputPower;
    }

    /**
     * <p>
     * 转换能量消耗为kW
     * </p>
     * 修改toUse值
     */
    @Inject(
            method = "onUpdateServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/machine/TileEntityResistiveHeater;setActive(Z)V"
            )
    )
    public void anvilCraftTransducers$setEnergyPerTick(CallbackInfoReturnable<Boolean> cir, @Local LocalLongRef toUse) {
        long energyPerTick = energyContainer.getEnergyPerTick();
        if (canFunction()) {
            inputPower = (int) (energyPerTick / AnvilCraftTransducers.CONFIG.transducers);
            if (MekPowerConsumerUtils.getEnergyPerTick(grid) == 0) {
                heatCapacitor.handleHeat(energyPerTick * MekanismConfig.general.resistiveHeaterEfficiency.get());
                toUse.set(energyPerTick);
            }
        } else {
            inputPower = 0;
        }
    }
}
