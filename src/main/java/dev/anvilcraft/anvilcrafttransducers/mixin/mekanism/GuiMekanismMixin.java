package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.VirtualSlotContainerScreen;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMekanism.class)
public abstract class GuiMekanismMixin<CONTAINER extends AbstractContainerMenu> extends VirtualSlotContainerScreen<CONTAINER> {
    public GuiMekanismMixin(CONTAINER container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$init(CallbackInfo ci) {
        children().stream().filter(children -> children instanceof GuiVerticalPowerBar).findFirst().ifPresent(this::removeWidget);
    }
}
