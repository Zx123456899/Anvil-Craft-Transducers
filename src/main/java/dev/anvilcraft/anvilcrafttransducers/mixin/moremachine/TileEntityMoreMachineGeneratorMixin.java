package dev.anvilcraft.anvilcrafttransducers.mixin.moremachine;

import dev.anvilcraft.anvilcrafttransducers.mixin.mekanism.BasicEnergyContainerMixin;
import dev.anvilcraft.anvilcrafttransducers.mixinapi.IExternalPowerManager;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

@Mixin(TileEntityMoreMachineGenerator.class)
public abstract class TileEntityMoreMachineGeneratorMixin extends TileEntityMekanism implements IPowerProducer {
    @Shadow
    public abstract BasicEnergyContainer getEnergyContainer();
    @Unique
    private PowerGrid grid = null;

    public TileEntityMoreMachineGeneratorMixin(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
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
        this.grid = grid;
    }

    @Override
    public int getOutputPower() {
        return ((IExternalPowerManager) getEnergyContainer()).getOutputPower();
    }

    /**
     * 在能量容器初始化之后，将 tile 引用注入到 BasicEnergyContainer 中，
     * 使 BasicEnergyContainerMixin 能够通过 getMachine() 获取到正确的 TileEntity 和 PowerGrid。
     */
    @Inject(
            method = "getInitialEnergyContainers",
            at = @At("RETURN")
    )
    public void anvilCraftTransducers$getInitialEnergyContainers(IContentsListener listener, CallbackInfoReturnable<IEnergyContainerHolder> cir) {
        BasicEnergyContainerMixin mixinInstance = (BasicEnergyContainerMixin) (Object) getEnergyContainer();
        mixinInstance.anvilCraftTransducers$setTile(this);
    }
}
