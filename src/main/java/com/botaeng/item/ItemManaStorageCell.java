package com.botaeng.item;

import java.util.Collections;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import appeng.api.stacks.AEKeyType;
import appeng.items.materials.MaterialType;
import appeng.items.storage.AbstractStorageCell;
import appeng.util.InventoryAdaptor;

import com.botaeng.BotaEngItems;
import com.botaeng.ManaTier;
import com.botaeng.me.ManaKeyType;

/**
 * A mana cell: a mana component in a mana cell housing. Bytes per type and idle drain are those of AE2UD's
 * fluid cells of the same size.
 */
public class ItemManaStorageCell extends AbstractStorageCell {

    private final ManaTier tier;

    /**
     * @param universalCounterpart only fills AE2UD's constructor; the component that comes back is the mana one
     */
    public ItemManaStorageCell(final ManaTier tier, final MaterialType universalCounterpart) {
        super(universalCounterpart, tier.kilobytes);
        this.tier = tier;
    }

    @Override
    public ItemStack getComponent() {
        return new ItemStack(BotaEngItems.COMPONENTS.get(this.tier));
    }

    @Override
    public int getBytesPerType(final ItemStack cellItem) {
        return this.tier.kilobytes * 8;
    }

    @Override
    public double getIdleDrain() {
        return 0.5 * (this.tier.ordinal() + 1);
    }

    @Override
    public Set<AEKeyType> getKeyTypes() {
        return Collections.singleton(ManaKeyType.INSTANCE);
    }

    @Override
    public int getTotalTypes(final ItemStack cellItem) {
        return 1;
    }

    @Override
    protected void dropEmptyStorageCellCase(final InventoryAdaptor ia, final EntityPlayer player) {
        final ItemStack extra = ia.addItems(new ItemStack(BotaEngItems.MANA_CELL_HOUSING));
        if (!extra.isEmpty()) {
            player.dropItem(extra, false);
        }
    }

    @Override
    public ItemStack getContainerItem(final ItemStack itemStack) {
        return new ItemStack(BotaEngItems.MANA_CELL_HOUSING);
    }
}
