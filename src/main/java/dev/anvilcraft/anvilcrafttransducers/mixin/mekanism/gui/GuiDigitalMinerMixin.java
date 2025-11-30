package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.machine.GuiDigitalMiner;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiDigitalMiner.class)
public abstract class GuiDigitalMinerMixin extends GuiMekanismTile<TileEntityDigitalMiner, MekanismTileContainer<TileEntityDigitalMiner>> {
    public GuiDigitalMinerMixin(MekanismTileContainer<TileEntityDigitalMiner> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(
            method = "lambda$addGuiElements$4",
            at = @At("RETURN"),
            cancellable = true
    )
    public void anvilCraftTransducers$setNotEnoughEnergy(CallbackInfoReturnable<Boolean> cir) {
        // 返回是否使用能量来判断机器是否有足够能量
        cir.setReturnValue(!tile.getActive());
    }
}
