package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.mixinapi.anvilcraft.IPowerGrid;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.integration.curios.CuriosIntegration;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.TileEntityChargepad;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityElectricPump;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.function.Predicate;

@Mixin(TileEntityChargepad.class)
public abstract class TileEntityChargepadMixin extends TileEntityMekanism implements IPowerConsumer {
    @Shadow
    @Final
    private static Predicate<LivingEntity> CHARGE_PREDICATE;
    @Shadow
    private MachineEnergyContainer<TileEntityElectricPump> energyContainer;
    @Unique
    private PowerGrid grid = null;

    public TileEntityChargepadMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        ((IExternalPowerManager) energyContainer).markPowerChange();
        ((IExternalPowerManager) energyContainer).setInputPower(0);
        this.grid = grid;
    }

    @Override
    public int getInputPower() {
        return ((IExternalPowerManager) energyContainer).getInputPower();
    }

    @Overwrite
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        boolean active = false;
        IExternalPowerManager externalPowerManager = (IExternalPowerManager) energyContainer;
        if (!energyContainer.isEmpty()) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    worldPosition.getX() + 1, worldPosition.getY() + 0.4, worldPosition.getZ() + 1), CHARGE_PREDICATE);
            int power = 0;
            for (LivingEntity entity : entities) {
                if (entity instanceof Player) {
                    IItemHandler itemHandler = Capabilities.ITEM.getCapability(entity);
                    if (chargeHandler(itemHandler)) {
                        active = true;
                    } else if (Mekanism.hooks.curios.isLoaded()) {
                        if (chargeHandler(CuriosIntegration.getCuriosInventory(entity))) {
                            active = true;
                        }
                    }
                } else if (provideEnergy(EnergyCompatUtils.getStrictEnergyHandler(entity))) {
                    active = true;
                }
                power += externalPowerManager.getNoChangeInputPower();
            }
            externalPowerManager.setInputPower(power);
            if (power <= 0) {
                active = false;
            }
        } else {
            externalPowerManager.setInputPower(0);
        }
        if (active != getActive()) {
            setActive(active);
        }
        return sendUpdatePacket;
    }

    @Overwrite
    private boolean chargeHandler(@Nullable IItemHandler itemHandler) {
        if (itemHandler != null) {
            int slots = itemHandler.getSlots();
            for (int slot = 0; slot < slots; slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                provideEnergy(EnergyCompatUtils.getStrictEnergyHandler(stack));
                if (((IExternalPowerManager) energyContainer).getNoChangeInputPower() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Overwrite
    private boolean provideEnergy(@Nullable IStrictEnergyHandler energyHandler) {
        if (energyHandler == null) {
            return false;
        }
        long energyToGive = energyContainer.getEnergyPerTick();
        long simulatedRemainder = energyHandler.insertEnergy(energyToGive, Action.SIMULATE);
        long needEnergy = energyToGive - simulatedRemainder;
        IExternalPowerManager externalPowerManager = ((IExternalPowerManager) energyContainer);
        externalPowerManager.setInputPower(0);
        if (needEnergy > 0
                && energyContainer.extract(needEnergy, Action.EXECUTE, AutomationType.INTERNAL) > 0
                && grid instanceof IPowerGrid powerGrid
                && !powerGrid.canChange()
                && externalPowerManager.isPowerChange()
                && grid.isWorking()) {
            energyHandler.insertEnergy(needEnergy, Action.EXECUTE);
            if (energyHandler.getNeededEnergy(0) == 0) {
                externalPowerManager.markPowerChange();
                externalPowerManager.setInputPower(0);
            }
            return true;
        }
        return false;
    }
}