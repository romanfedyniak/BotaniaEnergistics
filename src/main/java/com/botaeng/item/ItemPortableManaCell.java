package com.botaeng.item;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;

import vazkii.botania.api.mana.IManaItem;

import appeng.api.config.Actionable;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.StorageCell;
import appeng.items.tools.powered.ToolPortableCell;
import appeng.me.helpers.BaseActionSource;

import com.botaeng.me.ManaKey;
import com.botaeng.me.ManaKeyType;

/**
 * AE2UD's portable cell holding mana, which Botania also takes for a mana tablet: tools draw on it, it charges
 * other mana items, and a pool fills or empties it. None of that costs AE energy, as it costs a tablet nothing.
 * <p>
 * Botania counts mana in an int, so a cell holding more than two billion shows two billion; the rest comes into
 * view as it is spent.
 */
public class ItemPortableManaCell extends ToolPortableCell implements IManaItem {

    private final int maxMana;

    public ItemPortableManaCell(final int kilobytes) {
        super(kilobytes, () -> ManaKeyType.INSTANCE);
        final long bytes = (long) kilobytes * 512 - (long) kilobytes * 8;
        this.maxMana = (int) Math.min(Integer.MAX_VALUE, bytes * ManaKeyType.INSTANCE.getAmountPerByte());
    }

    @Override
    public int getTotalTypes(final ItemStack cellItem) {
        return 1;
    }

    @Override
    public int getMana(final ItemStack stack) {
        final StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (cell == null) {
            return 0;
        }
        return (int) Math.min(Integer.MAX_VALUE, cell.getAvailableStacks().get(ManaKey.INSTANCE));
    }

    @Override
    public int getMaxMana(final ItemStack stack) {
        return this.maxMana;
    }

    @Override
    public void addMana(final ItemStack stack, final int mana) {
        // Only the server's cell has a contents file; a change made on the client would be written into the item.
        if (mana == 0 || FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        final StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (cell == null) {
            return;
        }
        if (mana > 0) {
            cell.insert(ManaKey.INSTANCE, mana, Actionable.MODULATE, new BaseActionSource());
        } else {
            cell.extract(ManaKey.INSTANCE, -(long) mana, Actionable.MODULATE, new BaseActionSource());
        }
    }

    @Override
    public boolean canReceiveManaFromPool(final ItemStack stack, final TileEntity pool) {
        return true;
    }

    @Override
    public boolean canReceiveManaFromItem(final ItemStack stack, final ItemStack otherStack) {
        return true;
    }

    @Override
    public boolean canExportManaToPool(final ItemStack stack, final TileEntity pool) {
        return true;
    }

    @Override
    public boolean canExportManaToItem(final ItemStack stack, final ItemStack otherStack) {
        return true;
    }

    @Override
    public boolean isNoExport(final ItemStack stack) {
        return false;
    }
}
