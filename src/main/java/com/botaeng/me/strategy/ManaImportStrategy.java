package com.botaeng.me.strategy;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;

import com.botaeng.me.ManaKey;
import com.botaeng.me.ManaKeyType;

/**
 * What an import bus draws out of the pool it faces. Only a pool gives mana back.
 */
public class ManaImportStrategy implements StackImportStrategy {

    private final World world;
    private final BlockPos fromPos;

    ManaImportStrategy(final World world, final BlockPos fromPos) {
        this.world = world;
        this.fromPos = fromPos;
    }

    @Override
    public boolean transfer(final StackTransferContext context) {
        if (!context.hasOperationsLeft() || !context.getFilter().matches(ManaKey.INSTANCE)) {
            return false;
        }
        final IManaReceiver receiver = ManaReceivers.find(this.world, this.fromPos);
        if (!(receiver instanceof IManaPool pool)) {
            return false;
        }

        final int amountPerOperation = ManaKeyType.INSTANCE.getAmountPerOperation();
        final long budget = (long) context.getOperationsRemaining() * amountPerOperation;
        final var internal = context.getInternalStorage();
        final var source = context.getActionSource();

        final long available = ManaReceivers.extract(pool, budget, true);
        final long acceptable = internal.insert(ManaKey.INSTANCE, available, Actionable.SIMULATE, source);
        if (acceptable <= 0) {
            return false;
        }
        final long drawn = ManaReceivers.extract(pool, acceptable, false);
        final long inserted = internal.insert(ManaKey.INSTANCE, drawn, Actionable.MODULATE, source);
        if (inserted < drawn) {
            // Hand back what did not fit rather than void it.
            ManaReceivers.insert(pool, drawn - inserted, false);
        }

        context.reduceOperationsRemaining(Math.max(1, inserted / amountPerOperation));
        return inserted > 0;
    }

    public static StackImportStrategy create(final World world, final BlockPos fromPos, final EnumFacing fromSide) {
        return new ManaImportStrategy(world, fromPos);
    }
}
