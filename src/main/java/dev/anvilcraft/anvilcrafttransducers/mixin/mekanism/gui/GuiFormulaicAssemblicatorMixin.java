package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism.gui;

import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.machine.GuiFormulaicAssemblicator;
import mekanism.common.inventory.container.tile.FormulaicAssemblicatorContainer;
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiFormulaicAssemblicator.class)
public abstract class GuiFormulaicAssemblicatorMixin extends GuiConfigurableTile<TileEntityFormulaicAssemblicator, FormulaicAssemblicatorContainer> {
    public GuiFormulaicAssemblicatorMixin(FormulaicAssemblicatorContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(
            method = "lambda$addGuiElements$0",
            at = @At("RETURN"),
            cancellable = true
    )
    public void anvilCraftTransducers$setNotEnoughEnergy(CallbackInfoReturnable<Boolean> cir) {
        // 返回是否使用能量来判断机器是否有足够能量
        cir.setReturnValue(!tile.usedEnergy());
    }
}
