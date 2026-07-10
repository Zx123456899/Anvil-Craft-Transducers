package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IOriginalBehavior;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.RobitEnergyContainer;
import mekanism.common.entity.EntityRobit;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import mekanism.common.tile.machine.TileEntitySolarNeutronActivator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {
        TileEntityLaserTractorBeam.class,
        TileEntityLaserAmplifier.class,
        RobitEnergyContainer.class,
        EntityRobit.class,
        TileEntitySolarNeutronActivator.class
})
public class OriginalBehavior implements IOriginalBehavior {
}