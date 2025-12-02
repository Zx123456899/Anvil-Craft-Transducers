package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.ILaserEnergyContainer;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.LaserEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(LaserEnergyContainer.class)
public abstract class LaserEnergyContainerMixin<TILE extends TileEntityMekanism> extends BasicEnergyContainer implements ILaserEnergyContainer<TILE> {
    @Unique
    private TILE tile;

    public LaserEnergyContainerMixin(long maxEnergy, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert, @Nullable IContentsListener listener) {
        super(maxEnergy, canExtract, canInsert, listener);
    }

    @Inject(
            method = "create",
            at = @At("RETURN"),
            cancellable = true
    )
    private static <TILE extends TileEntityMekanism> void anvilCraftTransducers$create(Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert, TileEntityMekanism tile, @Nullable IContentsListener listener, CallbackInfoReturnable<LaserEnergyContainer> cir) {
        LaserEnergyContainer energyContainer = cir.getReturnValue();
        // 我讨厌泛型
        ((ILaserEnergyContainer<TILE>) energyContainer).setTile((TILE) tile);
        cir.setReturnValue(energyContainer);
    }

    @Override
    public void setTile(TILE tile) {
        this.tile = tile;
    }

    @Override
    public TILE getTile() {
        return tile;
    }
}
