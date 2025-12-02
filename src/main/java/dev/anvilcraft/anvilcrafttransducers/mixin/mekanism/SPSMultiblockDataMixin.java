package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.lib.multiblock.Structure;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SPSMultiblockData.class)
public abstract class SPSMultiblockDataMixin extends MultiblockData {
    @Shadow
    public boolean couldOperate;
    @Shadow
    public double lastProcessed;
    @Shadow
    public long lastReceivedEnergy;
    @Unique
    IPowerConsumer powerConsumer = null;

    public SPSMultiblockDataMixin(BlockEntity tile) {
        super(tile);
    }

    @Shadow
    protected abstract long process(long operations);

    @Inject(
            method = "onCreated",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$onCreated(Level world, CallbackInfo ci) {
        powerConsumer = locations.stream()
                .map(blockPos -> getLevel().getBlockEntity(blockPos))
                .filter(blockEntity -> blockEntity instanceof IPowerConsumer)
                .map(blockEntity -> (IPowerConsumer) blockEntity)
                .findFirst()
                .orElseGet(null);
    }

    @Override
    public void remove(Level world, Structure oldStructure) {
        super.remove(world, oldStructure);
        powerConsumer = null;
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/content/sps/SPSMultiblockData;kill(Lnet/minecraft/world/level/Level;)V"
            )
    )
    public void anvilCraftTransducers$tick(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (
                couldOperate
                        && powerConsumer != null
                        && powerConsumer.getGrid() instanceof IPowerGrid powerGrid
                        && powerConsumer.getGrid().isWorking()
                        && !powerGrid.canChange()
        ) {
            int spsInputPerAntimatter = MekanismConfig.general.spsInputPerAntimatter.get();
            lastProcessed = spsInputPerAntimatter;
            lastReceivedEnergy = MekanismConfig.general.spsEnergyPerInput.get();
            process(spsInputPerAntimatter);
        }
    }
}
