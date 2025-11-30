package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.util.MekPowerConsumerUtils;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityElectricPump;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityElectricPump.class)
public class TileEntityElectricPumpMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow private boolean usedEnergy;
    @Unique
    private PowerGrid grid = null;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;

    public TileEntityElectricPumpMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
     *
     * @return 0:通过 1:失败
     */
    @ModifyVariable(
            method = "onUpdateServer",
            at = @At("STORE"),
            ordinal = 1
    )
    public long anvilCraftTransducers$setEnergyPerTick(long value) {
        inputPower = (int) (value / AnvilCraftTransducers.CONFIG.transducers);
        long energyPerTick = MekPowerConsumerUtils.getEnergyPerTick(grid);
        usedEnergy = energyPerTick == 0;
        return energyPerTick;
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

    @Redirect(
            method = "onUpdateServer",
            at = @At(
                    value = "FIELD",
                    target = "Lmekanism/common/tile/machine/TileEntityElectricPump;usedEnergy:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void anvilCraftTransducers$setUsedEnergy(TileEntityElectricPump instance, boolean value) {
        // 取消此处的赋值，逻辑转移上方的方法中
    }
}