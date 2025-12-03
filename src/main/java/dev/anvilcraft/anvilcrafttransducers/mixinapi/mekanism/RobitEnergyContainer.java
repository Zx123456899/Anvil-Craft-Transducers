package dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class RobitEnergyContainer extends BasicEnergyContainer {
    public RobitEnergyContainer(long maxEnergy, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert, @Nullable IContentsListener listener) {
        super(maxEnergy, canExtract, canInsert, listener);
    }

    public static RobitEnergyContainer create(long maxEnergy, @Nullable IContentsListener listener) {
        return new RobitEnergyContainer(maxEnergy, notExternal, ConstantPredicates.alwaysTrue(), listener);
    }
}
