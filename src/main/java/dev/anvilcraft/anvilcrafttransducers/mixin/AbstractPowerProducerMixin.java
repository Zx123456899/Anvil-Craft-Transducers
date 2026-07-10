package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public abstract class AbstractPowerProducerMixin implements IPowerProducer {
    @Unique
    private PowerGrid grid = null;
    @Unique
    private final String modId;

    protected AbstractPowerProducerMixin(String modId) {
        this.modId = modId;
    }

    protected abstract IExternalPowerManager getEnergyContainer();

    @Override
    public @Nullable Level getCurrentLevel() {
        return null;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ZERO;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public int getOutputPower() {
        return getEnergyContainer().getOutputPower();
    }
}