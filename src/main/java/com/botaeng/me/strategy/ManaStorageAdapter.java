package com.botaeng.me.strategy;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageChangeSource;
import appeng.api.storage.MEStorage;
import appeng.me.storage.ITickingMonitor;
import appeng.me.storage.StorageChangeListeners;

import com.botaeng.me.ManaKey;

/**
 * A Botania block mounted on a network by a storage bus. A pool is storage both ways; anything else only takes
 * mana, and shows none.
 */
public class ManaStorageAdapter implements MEStorage, ITickingMonitor, IStorageChangeSource {

    private final IManaReceiver receiver;
    @Nullable
    private final Runnable changeListener;
    private final StorageChangeListeners listeners = new StorageChangeListeners();

    private long cached;

    ManaStorageAdapter(final IManaReceiver receiver, @Nullable final Runnable changeListener) {
        this.receiver = receiver;
        this.changeListener = changeListener;
        this.cached = this.read();
    }

    @Override
    public void addChangeListener(final Listener listener) {
        this.listeners.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(final Listener listener) {
        this.listeners.removeChangeListener(listener);
    }

    @Override
    public long insert(final AEKey what, final long amount, final Actionable mode, final IActionSource source) {
        if (!ManaKey.is(what)) {
            return 0;
        }
        final long inserted = ManaReceivers.insert(this.receiver, amount, mode == Actionable.SIMULATE);
        if (inserted > 0 && mode == Actionable.MODULATE) {
            this.refresh();
        }
        return inserted;
    }

    @Override
    public long extract(final AEKey what, final long amount, final Actionable mode, final IActionSource source) {
        if (!ManaKey.is(what) || !(this.receiver instanceof IManaPool pool)) {
            return 0;
        }
        final long extracted = ManaReceivers.extract(pool, amount, mode == Actionable.SIMULATE);
        if (extracted > 0 && mode == Actionable.MODULATE) {
            this.refresh();
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(final KeyCounter out) {
        if (this.cached > 0) {
            out.add(ManaKey.INSTANCE, this.cached);
        }
    }

    @Override
    public TickRateModulation onTick() {
        return this.update() ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    private void refresh() {
        this.update();
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }

    /**
     * Everything this adapter sees passes through here, its own transfers as much as a spreader filling the
     * pool, so this is the one place that tells the network what moved.
     *
     * @return whether the amount changed
     */
    private boolean update() {
        final long fresh = this.read();
        if (fresh == this.cached) {
            return false;
        }
        final KeyCounter before = new KeyCounter();
        final KeyCounter after = new KeyCounter();
        before.add(ManaKey.INSTANCE, this.cached);
        after.add(ManaKey.INSTANCE, fresh);
        this.cached = fresh;
        this.listeners.postDiff(before, after);
        return true;
    }

    private long read() {
        return this.receiver instanceof IManaPool ? Math.max(0, this.receiver.getCurrentMana()) : 0;
    }

    /**
     * The {@link ExternalStorageStrategy} for mana; a storage bus reaches it through AE2UD, never directly.
     */
    public static final class Strategy implements ExternalStorageStrategy {

        private final World world;
        private final BlockPos fromPos;

        public Strategy(final World world, final BlockPos fromPos, final EnumFacing fromSide) {
            this.world = world;
            this.fromPos = fromPos;
        }

        @Nullable
        @Override
        public MEStorage createWrapper(final boolean extractableOnly, final Runnable injectOrExtractCallback) {
            final IManaReceiver receiver = ManaReceivers.find(this.world, this.fromPos);
            return receiver == null ? null : new ManaStorageAdapter(receiver, injectOrExtractCallback);
        }
    }
}
