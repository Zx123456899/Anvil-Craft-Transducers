package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import mekanism.common.inventory.warning.WarningTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(WarningTracker.class)
public interface WarningTrackerAccessor {
    @Accessor("warnings")
    Map<WarningTracker.WarningType, List<BooleanSupplier>> getWarnings();
}
