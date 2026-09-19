package com.botaeng.me;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageSink;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import com.botaeng.BotaEngItems;

/**
 * The creative mana cell's contents: endless mana out, anything put in gone, and never a change to report. The
 * same as an AE2UD creative cell given mana, only already given it.
 */
public final class CreativeManaCell implements StorageCell, IStorageSink {

    /** What AE2UD's creative cell shows, large enough to read as endless and small enough to add up. */
    private static final long STORED_AMOUNT = (1L << 52) - 1;

    private final ItemStack stack;

    private CreativeManaCell(final ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long insert(final AEKey what, final long amount, final Actionable mode, final IActionSource source) {
        return ManaKey.is(what) ? amount : 0;
    }

    @Override
    public long extract(final AEKey what, final long amount, final Actionable mode, final IActionSource source) {
        return ManaKey.is(what) ? amount : 0;
    }

    @Override
    public void getAvailableStacks(final KeyCounter out) {
        out.add(ManaKey.INSTANCE, STORED_AMOUNT);
    }

    @Override
    public boolean isPreferredStorageFor(final AEKey what, final IActionSource source) {
        return ManaKey.is(what);
    }

    @Override
    public Set<AEKeyType> getSupportedKeyTypes() {
        return Collections.singleton(ManaKeyType.INSTANCE);
    }

    @Override
    public CellState getStatus() {
        return CellState.TYPES_FULL;
    }

    @Override
    public double getIdleDrain() {
        return 0;
    }

    @Override
    public boolean canFitInsideCell() {
        return false;
    }

    @Override
    public void persist() {
        // Nothing changes, so nothing to save.
    }

    @Override
    public ITextComponent getDescription() {
        return new TextComponentString(this.stack.getDisplayName());
    }

    public static final class Handler implements ICellHandler {

        @Override
        public boolean isCell(final ItemStack is) {
            return !is.isEmpty() && is.getItem() == BotaEngItems.CREATIVE_MANA_CELL;
        }

        @Nullable
        @Override
        public StorageCell getCellInventory(final ItemStack is, @Nullable final ISaveProvider host) {
            return this.isCell(is) ? new CreativeManaCell(is) : null;
        }
    }
}
