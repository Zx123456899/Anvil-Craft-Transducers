package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.api.anvilcraft.IPowerGrid;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.math.MathUtils;
import mekanism.common.content.teleporter.TeleporterFrequency;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
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

import java.util.List;

import static mekanism.common.tile.TileEntityTeleporter.TeleportInfo;
import static mekanism.common.tile.TileEntityTeleporter.TeleporterStatus;

@Mixin(TileEntityTeleporter.class)
public abstract class TileEntityTeleporterMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    public TeleporterStatus status;
    @Unique
    private PowerGrid grid = null;
    /**
     * 用电量缓存
     */
    @Unique
    private int inputPower = 0;
    @Unique
    private boolean changePower = false;

    public TileEntityTeleporterMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    /**
     * 转换能量消耗单位为kW
     */
    @Inject(
            method = "calculateEnergyCost(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/GlobalPos;)J",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void anvilCraftTransducers$calculateEnergyCost(Entity entity, Level targetWorld, GlobalPos coords, CallbackInfoReturnable<Long> cir, @Local int passengerCount, @Local long energyCost) {
        long newEnergyCost = energyCost / AnvilCraftTransducers.CONFIG.transducers;
        cir.setReturnValue(passengerCount > 0 ? MathUtils.multiplyClamped(newEnergyCost, 1 + passengerCount) : newEnergyCost);
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
        // 当电网调用此方法时，代表着电网正在更新
        // 设置标记防止重复计算
        changePower = inputPower > 0;
        return inputPower;
    }

    @Redirect(
            method = "onUpdateServer",
            at = @At(
                    value = "FIELD",
                    target = "Lmekanism/common/tile/TileEntityTeleporter;shouldRender:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void anvilCraftTransducers$setShouldRender(TileEntityTeleporter instance, boolean value) {
        // 当状态是无能量或者就绪时，传送门的渲染转为由电网是否过载控制
        // 此功能会导致视觉效果延迟，不知道大部分玩家是否接受
        // 后续可能会考虑删除
        instance.shouldRender = (status == TeleporterStatus.NOT_ENOUGH_ENERGY || status == TeleporterStatus.READY) ? grid.isWorking() : value;
    }

    @Inject(
            method = "canTeleport",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/capabilities/energy/MachineEnergyContainer;extract(JLmekanism/api/Action;Lmekanism/api/AutomationType;)J"
            ),
            cancellable = true
    )
    public void anvilCraftTransducers$canTeleport(TeleporterFrequency frequency, CallbackInfoReturnable<TileEntityTeleporter.TeleportInfo> cir, @Local long sum, @Local GlobalPos closestCoords, @Local List<Entity> toTeleport) {
        inputPower = (int) sum;
        IPowerGrid powerGrid = (IPowerGrid) grid;
        if (
                !powerGrid.canChange() && grid.isWorking()
                        // 在电网过载时，传送门作为电网组件本身会被计算在总耗能中
                        // 若后续电网恢复工作，常规检测就会重复计算能耗，所以此处应当检测检标记防止重复计算
                        && (grid.getRemaining() >= inputPower || changePower)
        ) {
            changePower = false;
            cir.setReturnValue(new TeleportInfo(TeleporterStatus.READY, closestCoords, toTeleport));
        }
    }
}
