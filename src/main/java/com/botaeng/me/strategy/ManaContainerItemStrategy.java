package com.botaeng.me.strategy;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import com.botaeng.BotaEngConfig;
import com.botaeng.me.ManaKey;

/**
 * A mana tablet, a mana ring or anything else Botania keeps mana in, filled and emptied against a terminal row
 * or a conversion monitor like a bucket.
 * <p>
 * As with blocks, {@link IManaItem#addMana} says nothing of what it took, so the change is measured. An item
 * Botania marks as never giving mana away is not emptied, and the blacklisted ones are not touched at all.
 */
public final class ManaContainerItemStrategy implements ContainerItemStrategy {

    private static final int MAX_PER_CALL = 1 << 30;

    @Nullable
    @Override
    public GenericStack getContainedStack(final ItemStack stack) {
        final IManaItem item = manaItem(stack);
        if (item == null || item.isNoExport(stack)) {
            return null;
        }
        final int mana = item.getMana(stack);
        return mana > 0 ? new GenericStack(ManaKey.INSTANCE, mana) : null;
    }

    @Nullable
    @Override
    public Context openContext(final ItemStack container) {
        final IManaItem item = manaItem(container);
        if (item == null) {
            return null;
        }
        final ItemStack one = container.copy();
        one.setCount(1);
        return new ManaContext(item, one);
    }

    @Nullable
    private static IManaItem manaItem(final ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IManaItem && !BotaEngConfig.isBlacklisted(stack)
                ? (IManaItem) stack.getItem()
                : null;
    }

    private static final class ManaContext implements Context {

        private final IManaItem item;
        private final ItemStack stack;

        private ManaContext(final IManaItem item, final ItemStack stack) {
            this.item = item;
            this.stack = stack;
        }

        @Override
        public long insert(final AEKey what, final long amount, final Actionable mode) {
            if (!ManaKey.is(what) || amount <= 0) {
                return 0;
            }
            final int before = this.item.getMana(this.stack);
            final long space = this.item.getMaxMana(this.stack) - (long) before;
            final int toAdd = (int) Math.min(Math.min(amount, MAX_PER_CALL), space);
            if (toAdd <= 0 || mode == Actionable.SIMULATE) {
                return Math.max(0, toAdd);
            }
            this.item.addMana(this.stack, toAdd);
            return Math.max(0, this.item.getMana(this.stack) - before);
        }

        @Override
        public long extract(final AEKey what, final long amount, final Actionable mode) {
            if (!ManaKey.is(what) || amount <= 0 || this.item.isNoExport(this.stack)) {
                return 0;
            }
            final int before = this.item.getMana(this.stack);
            final int toTake = (int) Math.min(Math.min(amount, MAX_PER_CALL), before);
            if (toTake <= 0 || mode == Actionable.SIMULATE) {
                return Math.max(0, toTake);
            }
            // A creative tablet reads full whatever is taken and never runs dry.
            if (this.isCreative()) {
                return toTake;
            }
            this.item.addMana(this.stack, -toTake);
            return Math.max(0, before - this.item.getMana(this.stack));
        }

        @Nullable
        @Override
        public GenericStack getExtractableContent() {
            if (this.item.isNoExport(this.stack)) {
                return null;
            }
            final int mana = this.item.getMana(this.stack);
            return mana > 0 ? new GenericStack(ManaKey.INSTANCE, mana) : null;
        }

        @Override
        public ItemStack getContainer() {
            return this.stack;
        }

        private boolean isCreative() {
            return this.stack.getItem() instanceof ICreativeManaProvider
                    && ((ICreativeManaProvider) this.stack.getItem()).isCreative(this.stack);
        }
    }
}
