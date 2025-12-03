package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism.RobitEnergyContainer;
import mekanism.api.IContentsListener;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.entity.EntityRobit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Mixin(EntityRobit.class)
public abstract class EntityRobitMixin {
    @Shadow
    @Final
    public static long MAX_ENERGY;
    @Mutable
    @Shadow
    @Final
    private List<IEnergyContainer> energyContainers;
    @Mutable
    @Shadow
    @Final
    private BasicEnergyContainer energyContainer;

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lmekanism/common/entity/EntityRobit;energyContainers:Ljava/util/List;"
            )
    )
    public void anvilCraftTransducers$setEnergyContainers(EntityRobit instance, List<IEnergyContainer> value, @Local IContentsListener recipeCacheUnpauseListener) {
        energyContainers = Collections.singletonList(energyContainer = RobitEnergyContainer.create(MAX_ENERGY, recipeCacheUnpauseListener));
    }
}
