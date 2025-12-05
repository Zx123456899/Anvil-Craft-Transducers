package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.VirtualSlotContainerScreen;
import mekanism.client.gui.element.bar.GuiHorizontalPowerBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.robit.GuiRobitMain;
import mekanism.common.inventory.warning.IWarningTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mekanism.common.inventory.warning.WarningTracker.WarningType.NOT_ENOUGH_ENERGY;
import static mekanism.common.inventory.warning.WarningTracker.WarningType.NOT_ENOUGH_ENERGY_REDUCED_RATE;

@Mixin(GuiMekanism.class)
public abstract class GuiMekanismMixin<CONTAINER extends AbstractContainerMenu> extends VirtualSlotContainerScreen<CONTAINER> {
    @Shadow
    private @Nullable IWarningTracker warningTracker;

    public GuiMekanismMixin(CONTAINER container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/client/gui/GuiMekanism;addGuiElements()V",
                    shift = At.Shift.AFTER
            )
    )
    public void anvilCraftTransducers$init(CallbackInfo ci) {
        if ((Object) this instanceof GuiRobitMain) return;
        children().removeIf(
                children -> children instanceof GuiVerticalPowerBar
                        || children instanceof GuiHorizontalPowerBar
                        || children instanceof GuiEnergyTab
        );
        if (warningTracker instanceof WarningTrackerAccessor warningTrackerAccessor) {
            warningTrackerAccessor.getWarnings().remove(NOT_ENOUGH_ENERGY);
            warningTrackerAccessor.getWarnings().remove(NOT_ENOUGH_ENERGY_REDUCED_RATE);
        }
    }
}
