package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MachineEnergyContainer.class)
public interface MachineEnergyContainerAccessor<Tile extends TileEntityMekanism> {
    /**
     * 获取{@link MachineEnergyContainer}所属的{@link TileEntityMekanism}
     *
     * @return 获取能量容器对应的设备
     */
    @Accessor("tile")
    Tile getTile();
}
