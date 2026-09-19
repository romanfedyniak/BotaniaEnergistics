package com.botaeng.mixin;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.api.mana.IManaReceiver;

import appeng.api.parts.IPartHost;
import appeng.tile.networking.TileCableBus;

import com.botaeng.part.ManaBurstTunnels;

/**
 * A spreader fires only at a receiver with room, so a cable bus with a burst tunnel on it has to look like one.
 * It keeps no mana: what a burst brings goes on through the tunnel.
 */
@Mixin(value = TileCableBus.class, remap = false)
public abstract class MixinTileCableBus implements IManaReceiver {

    @Override
    public boolean isFull() {
        return !ManaBurstTunnels.acceptsBursts((IPartHost) this);
    }

    @Override
    public void recieveMana(final int mana) {
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !this.isFull();
    }

    @Override
    public int getCurrentMana() {
        return 0;
    }
}
