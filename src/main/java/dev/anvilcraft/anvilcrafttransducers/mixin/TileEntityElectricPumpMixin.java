package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.api.anvilcraft.IPowerGrid;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityElectricPump.class)
public class TileEntityElectricPumpMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    private boolean usedEnergy;
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
     * 转换能量消耗为kW
     */
    @ModifyVariable(
            method = "onUpdateServer",
            at = @At("STORE"),
            ordinal = 1
    )
    public long anvilCraftTransducers$setEnergyPerTick(long value) {
        inputPower = (int) (value / AnvilCraftTransducers.CONFIG.transducers);
        if (!grid.isWorking()
                || grid instanceof IPowerGrid powerGrid && powerGrid.canChange()
        ) {
            usedEnergy = false;
            // 机器内部没有能量，返回1让检测失败
            return 1;
        }
        usedEnergy = true;
        // 机器内部没有能量，返回0让检测通过
        return 0;
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
        // 取消此处的赋值，逻辑转移上放的方法中
    }
}