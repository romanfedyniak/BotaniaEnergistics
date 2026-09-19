package com.botaeng.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaTrigger;

import appeng.block.networking.BlockCableBus;

import com.botaeng.part.ManaBurstTunnels;

/**
 * Botania tells a block, and not its tile, that a burst struck it.
 */
@Mixin(value = BlockCableBus.class, remap = false)
public abstract class MixinBlockCableBus implements IManaTrigger {

    @Override
    public void onBurstCollision(final IManaBurst burst, final World world, final BlockPos pos) {
        ManaBurstTunnels.onBurstCollision(burst, world, pos);
    }
}
