package dev.anvilcraft.anvilcrafttransducers.mixin.anvilcraft;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PowerGrid.class)
public class PowerGridMixin implements IPowerGrid {
    @Shadow
    private boolean changed;

    @Override
    public boolean canChange() {
        return changed;
    }
}
