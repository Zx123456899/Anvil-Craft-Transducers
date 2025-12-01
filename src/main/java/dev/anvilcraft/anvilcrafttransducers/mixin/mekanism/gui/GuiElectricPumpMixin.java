package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.machine.GuiElectricPump;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.machine.TileEntityElectricPump;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiElectricPump.class)
public abstract class GuiElectricPumpMixin extends GuiMekanismTile<TileEntityElectricPump, MekanismTileContainer<TileEntityElectricPump>> {
    public GuiElectricPumpMixin(MekanismTileContainer<TileEntityElectricPump> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(
            method = "addGuiElements",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$addGuiElements(CallbackInfo ci) {
        // 测试删除能量条显示
        // 不过我搞不懂为什么warning还能存在，他不是在guiBar里面吗......
        children().stream().filter(children -> children instanceof GuiVerticalPowerBar).findFirst().ifPresent(this::removeWidget);
    }
}
