package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityMekanism.class)
public abstract class TileEntityMekanismMixin extends BlockEntity {

    @Unique
    private boolean act$addedToGrid = false;

    public TileEntityMekanismMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    public void anvilCraftTransducers$onUpdateServer(CallbackInfoReturnable<Boolean> cir) {
        if (!act$addedToGrid && this instanceof IPowerComponent component && level != null && !level.isClientSide) {
            PowerGrid.addComponent(component);
            act$addedToGrid = true;
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    public void anvilCraftTransducers$setRemoved(CallbackInfo ci) {
        if (act$addedToGrid && this instanceof IPowerComponent component && level != null && !level.isClientSide) {
            PowerGrid.removeComponent(component);
            act$addedToGrid = false;
        }
    }
}
