package dev.anvilcraft.anvilcrafttransducers.mixinapi.mekanism;

import mekanism.common.tile.base.TileEntityMekanism;

public interface ILaserEnergyContainer<TILE extends TileEntityMekanism> {
    void setTile(TILE tile);

    TILE getTile();
}
