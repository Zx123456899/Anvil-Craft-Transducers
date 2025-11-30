package dev.anvilcraft.anvilcrafttransducers.util;

import dev.anvilcraft.anvilcrafttransducers.api.anvilcraft.IPowerGrid;
import dev.dubhe.anvilcraft.api.power.PowerGrid;

public class MekPowerConsumerUtils {
    public static int getEnergyPerTick(PowerGrid grid) {
        if (!grid.isWorking()
                || grid.getGenerate() <= 0
                || grid instanceof IPowerGrid powerGrid && powerGrid.canChange()
        ) {
            // 机器内部没有能量，返回1让检测失败
            return 1;
        }
        // 机器内部没有能量，返回0让检测通过
        return 0;
    }
}
