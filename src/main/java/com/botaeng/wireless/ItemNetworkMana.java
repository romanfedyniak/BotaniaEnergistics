package com.botaeng.wireless;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import vazkii.botania.api.mana.IManaItem;

import com.botaeng.BotaEngConfig;

/**
 * The stand-in {@link WirelessMana} hands Botania for a player's networks. Never in a creative tab or an
 * inventory; a stack of it given by command stands for nothing and holds no mana.
 */
public class ItemNetworkMana extends Item implements IManaItem {

    @Override
    public int getMana(final ItemStack stack) {
        final WirelessMana source = WirelessMana.of(stack);
        return source == null ? 0 : source.available();
    }

    @Override
    public int getMaxMana(final ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    /** Only ever drawn from. */
    @Override
    public void addMana(final ItemStack stack, final int mana) {
        final WirelessMana source = WirelessMana.of(stack);
        if (source != null && mana < 0) {
            source.extract(-mana);
        }
    }

    @Override
    public boolean canReceiveManaFromPool(final ItemStack stack, final TileEntity pool) {
        return false;
    }

    @Override
    public boolean canReceiveManaFromItem(final ItemStack stack, final ItemStack otherStack) {
        return false;
    }

    @Override
    public boolean canExportManaToPool(final ItemStack stack, final TileEntity pool) {
        return false;
    }

    /** A blacklisted item is charged only the way Botania means it to be, not from a whole network. */
    @Override
    public boolean canExportManaToItem(final ItemStack stack, final ItemStack otherStack) {
        return !BotaEngConfig.isBlacklisted(otherStack);
    }

    @Override
    public boolean isNoExport(final ItemStack stack) {
        return false;
    }
}
