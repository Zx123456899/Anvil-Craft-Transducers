package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.tile.TileEntityGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.LongSupplier;

@Mixin(TileEntityHeatGenerator.class)
public abstract class TileEntityHeatGeneratorMixin extends TileEntityGenerator {
    public TileEntityHeatGeneratorMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state, @NotNull LongSupplier maxOutput) {
        super(blockProvider, pos, state, maxOutput);
    }

    /**
     * 让热力发电机的被动发电检测能够识别铁砧工艺的被加热方块
     */
    @Inject(
            method = "getBoost",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
                    shift = At.Shift.AFTER
            )
    )
    public void anvilCraftTransducers$getBoost(CallbackInfoReturnable<Double> cir, @Local BlockPos.MutableBlockPos mutable, @Local(ordinal = 0) LocalIntRef lavaSides) {
        if (WorldUtils.getBlockState(level, mutable).filter(state -> state.is(ModBlockTags.HEATABLE_BLOCKS)).isPresent()) {
            lavaSides.set(lavaSides.get() + 1);
        }
    }
}
