package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.anvilcraft.anvilcrafttransducers.util.MekPowerConsumerUtils;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.TileEntityModificationStation;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityModificationStation.class)
public abstract class TileEntityModificationStationMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    private boolean usedEnergy;
    @Shadow private MachineEnergyContainer<TileEntityModificationStation> energyContainer;
    @Unique
    private PowerGrid grid = null;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;

    public TileEntityModificationStationMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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

    @ModifyExpressionValue(
            method = "onUpdateServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/capabilities/energy/MachineEnergyContainer;getEnergyPerTick()J"
            )
    )
    public long anvilCraftTransducers$modifyCheckEnergy(long original) {
        inputPower = (int) (energyContainer.getEnergyPerTick() / 10);
        long energyPerTick = MekPowerConsumerUtils.getEnergyPerTick(grid);
        usedEnergy = energyPerTick == 0;
        return energyPerTick;
    }

    @Inject(
            method = "onUpdateServer",
            at = @At("HEAD")
    )
    public void anvilCraftTransducers$onUpdateServer(CallbackInfoReturnable<Boolean> cir) {
        if (!canFunction()) {
            inputPower = 0;
        }
    }

    @Redirect(
            method = "onUpdateServer",
            at = @At(
                    value = "FIELD",
                    target = "Lmekanism/common/tile/TileEntityModificationStation;usedEnergy:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void anvilCraftTransducers$setUsedEnergy(TileEntityModificationStation instance, boolean value) {
        // 取消此处的赋值，逻辑转移上方的方法中
    }
}
