package dev.anvilcraft.anvilcrafttransducers.mixin;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public abstract class AbstractPowerConsumerMixin implements IPowerConsumer {
    @Unique
    private PowerGrid grid = null;
    @Unique
    private final String modId;

    protected AbstractPowerConsumerMixin(String modId) {
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
        if (getEnergyContainer() != null) {
            getEnergyContainer().markPowerChange();
        }
        this.grid = grid;
    }

    @Override
    public int getInputPower() {
        if (getEnergyContainer() != null) {
            return getEnergyContainer().getInputPower();
        }
        return 0;
    }
}