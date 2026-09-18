package com.botaeng.me.strategy;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.mana.IManaReceiver;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;

import com.botaeng.me.ManaKey;

/**
 * What an export bus, an interface or a machine pushes into any Botania block that takes mana: a pool, a
 * spreader, a runic altar, a terrestrial agglomeration plate.
 */
public class ManaExportStrategy implements StackExportStrategy {

    private final World world;
    private final BlockPos fromPos;

    ManaExportStrategy(final World world, final BlockPos fromPos) {
        this.world = world;
        this.fromPos = fromPos;
    }

    @Override
    public long transfer(final StackTransferContext context, final AEKey what, final long maxAmount) {
        if (!ManaKey.is(what) || maxAmount <= 0) {
            return 0;
        }
        final IManaReceiver receiver = ManaReceivers.find(this.world, this.fromPos);
        if (receiver == null) {
            return 0;
        }

        final var internal = context.getInternalStorage();
        final var source = context.getActionSource();
        final long wanted = ManaReceivers.insert(receiver, maxAmount, true);
        if (wanted <= 0) {
            return 0;
        }
        // Taken first and the rest handed back: a block cannot be asked to give mana back once it has it.
        final long extracted = internal.extract(what, wanted, Actionable.MODULATE, source);
        if (extracted <= 0) {
            return 0;
        }
        final long taken = ManaReceivers.insert(receiver, extracted, false);
        if (taken < extracted) {
            internal.insert(what, extracted - taken, Actionable.MODULATE, source);
        }
        return taken;
    }

    @Override
    public long push(final AEKey what, final long maxAmount, final Actionable mode) {
        if (!ManaKey.is(what)) {
            return 0;
        }
        final IManaReceiver receiver = ManaReceivers.find(this.world, this.fromPos);
        return receiver == null ? 0 : ManaReceivers.insert(receiver, maxAmount, mode == Actionable.SIMULATE);
    }

    public static StackExportStrategy create(final World world, final BlockPos fromPos, final EnumFacing fromSide) {
        return new ManaExportStrategy(world, fromPos);
    }
}
