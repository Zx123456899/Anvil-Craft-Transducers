package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import dev.anvilcraft.anvilcrafttransducers.util.MekPowerConsumerUtils;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntitySeismicVibrator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntitySeismicVibrator.class)
public abstract class TileEntitySeismicVibratorMixin extends TileEntityMekanism implements IPowerConsumer {
    @Unique
    private PowerGrid grid = null;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;

    public TileEntitySeismicVibratorMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
     * 修改EnergyPerTick的返回值
     */
    @Inject(
            method = "onUpdateServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/capabilities/energy/MachineEnergyContainer;extract(JLmekanism/api/Action;Lmekanism/api/AutomationType;)J",
                    ordinal = 0
            )
    )
    public void anvilCraftTransducers$setEnergyPerTick(CallbackInfoReturnable<Boolean> cir, @Local LocalLongRef energyPerTick) {
        inputPower = (int) (energyPerTick.get() / 10);
        energyPerTick.set(MekPowerConsumerUtils.getEnergyPerTick(grid));
    }

    @Inject(
            method = "onUpdateServer",
            at = @At("HEAD")
    )
    public void anvilCraftTransducers$onUpdateServer(CallbackInfoReturnable<Boolean> cir){
        if (!canFunction()) {
            inputPower = 0;
        }
    }
}
