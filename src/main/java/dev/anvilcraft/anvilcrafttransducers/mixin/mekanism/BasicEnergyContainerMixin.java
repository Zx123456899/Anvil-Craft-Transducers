package dev.anvilcraft.anvilcrafttransducers.mixin.mekanism;

import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
import dev.anvilcraft.anvilcrafttransducers.api.mekanism.IMekPowerProducer;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicEnergyContainer.class)
public abstract class BasicEnergyContainerMixin implements IEnergyContainer, IMekPowerProducer {
    /**
     * 发电量缓存
     */
    @Unique
    private int outputPower = 0;

    @Override
    public int getOutputPower() {
        return outputPower;
    }

    /**
     * {@link BasicEnergyContainer#insert}
     *
     * <p>
     * 拦截了电量往能量容器输入
     * </p>
     *
     * <p>
     * 不知道只为EXECUTE动作设置发电量缓存会不会出问题，但就这样定了（）
     * </p>
     */
    @Inject(
            method = "insert",
            at = @At("HEAD"),
            cancellable = true
    )
    public void anvilCraftTransducers$insert(long amount, Action action, AutomationType automationType, CallbackInfoReturnable<Long> cir) {
        if (action == Action.EXECUTE) outputPower = (int) (amount / AnvilCraftTransducers.CONFIG.transducers);
        cir.setReturnValue(0L);
    }
}
