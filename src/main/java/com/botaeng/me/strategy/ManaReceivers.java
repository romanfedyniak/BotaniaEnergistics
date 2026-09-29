package com.botaeng.me.strategy;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.common.block.tile.mana.TileDistributor;
import vazkii.botania.common.block.tile.mana.TileSpreader;

import appeng.api.parts.IPartHost;

import com.botaeng.tile.TileFluixManaPool;

/**
 * How the network puts mana into Botania's blocks and takes it out.
 * <p>
 * Botania's {@link IManaReceiver#recieveMana} returns nothing and quietly drops whatever does not fit, so what
 * a block took is measured as the change in its mana. Only a pool gives mana back; nothing else in Botania
 * can be drawn from.
 */
public final class ManaReceivers {

    /** Keeps {@code getCurrentMana() + amount} inside an int in every receiver. */
    private static final int MAX_PER_CALL = 1 << 30;

    private ManaReceivers() {
    }

    /**
     * @return the receiver at that position, or null if there is none the network can work with
     */
    @Nullable
    public static IManaReceiver find(final World world, final BlockPos pos) {
        if (world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) == null) {
            return null;
        }
        final TileEntity tile = world.getTileEntity(pos);
        // A distributor passes mana on to the pools around it and never holds any, so what it took cannot be
        // measured: counting it as nothing would leave the network its mana while the pools got it too.
        // A fluix pool is a network's own mana, which a bus on it would only move in a circle.
        // A cable bus takes bursts for a tunnel and keeps none of what it is given.
        if (!(tile instanceof IManaReceiver) || tile instanceof TileDistributor || tile instanceof TileFluixManaPool
                || tile instanceof IPartHost) {
            return null;
        }
        return (IManaReceiver) tile;
    }

    /**
     * @return how much the receiver took, or with {@code simulate} how much it looks like it would take
     */
    public static long insert(final IManaReceiver receiver, final long amount, final boolean simulate) {
        if (amount <= 0 || receiver.isFull()) {
            return 0;
        }
        final int offered = (int) Math.min(amount, Math.min(space(receiver), MAX_PER_CALL));
        if (offered <= 0 || simulate) {
            return Math.max(0, offered);
        }
        final int before = receiver.getCurrentMana();
        receiver.recieveMana(offered);
        return Math.max(0, receiver.getCurrentMana() - before);
    }

    /**
     * Draws from a pool. A creative pool always reads full and loses nothing, so it gives without end, as it
     * does to a spreader.
     */
    public static long extract(final IManaPool pool, final long amount, final boolean simulate) {
        final int drawn = (int) Math.min(Math.min(amount, pool.getCurrentMana()), MAX_PER_CALL);
        if (drawn <= 0) {
            return 0;
        }
        if (!simulate) {
            pool.recieveMana(-drawn);
        }
        return drawn;
    }

    /**
     * Room left, where the block says; otherwise as much as is offered, and the measurement settles it.
     */
    private static long space(final IManaReceiver receiver) {
        if (receiver instanceof ISparkAttachable) {
            return ((ISparkAttachable) receiver).getAvailableSpaceForMana();
        }
        if (receiver instanceof TileSpreader) {
            return ((TileSpreader) receiver).getMaxMana() - receiver.getCurrentMana();
        }
        return Long.MAX_VALUE;
    }
}
