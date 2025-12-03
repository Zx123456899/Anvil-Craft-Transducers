package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.IOriginalBehavior;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.RobitEnergyContainer;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
        value = {
                TileEntityLaserTractorBeam.class,
                TileEntityLaserAmplifier.class,
                RobitEnergyContainer.class
        }
)
public class OriginalBehavior implements IOriginalBehavior {
}
