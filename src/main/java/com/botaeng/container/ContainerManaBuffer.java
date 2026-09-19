package com.botaeng.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import com.botaeng.tile.TileManaBuffer;

/**
 * A chest's slots over the buffer's items, with its mana and capacity sent along for the bar.
 */
public class ContainerManaBuffer extends Container {

    /** Window properties go over the wire as shorts, so each int travels in two halves. */
    private static final int MANA_LOW = 0;
    private static final int MANA_HIGH = 1;
    private static final int CAPACITY_LOW = 2;
    private static final int CAPACITY_HIGH = 3;

    private final TileManaBuffer tile;
    private int sentMana = -1;
    private int sentCapacity = -1;
    private int mana;
    private int capacity = 1;

    public ContainerManaBuffer(final InventoryPlayer player, final TileManaBuffer tile) {
        this.tile = tile;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new SlotItemHandler(tile.getItems(), col + row * 9, 8 + col * 18,
                        18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(player, col + row * 9 + 9, 8 + col * 18, 85 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(player, col, 8 + col * 18, 143));
        }
    }

    public int getMana() {
        return this.mana;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        final int mana = this.tile.getCurrentMana();
        final int capacity = TileManaBuffer.getCapacity();
        for (final IContainerListener listener : this.listeners) {
            if (mana != this.sentMana) {
                listener.sendWindowProperty(this, MANA_LOW, mana & 0xFFFF);
                listener.sendWindowProperty(this, MANA_HIGH, mana >>> 16);
            }
            if (capacity != this.sentCapacity) {
                listener.sendWindowProperty(this, CAPACITY_LOW, capacity & 0xFFFF);
                listener.sendWindowProperty(this, CAPACITY_HIGH, capacity >>> 16);
            }
        }
        this.sentMana = mana;
        this.sentCapacity = capacity;
    }

    @Override
    public void addListener(final IContainerListener listener) {
        super.addListener(listener);
        this.sentMana = -1;
        this.sentCapacity = -1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(final int id, final int data) {
        final int half = data & 0xFFFF;
        switch (id) {
            case MANA_LOW -> this.mana = (this.mana & 0xFFFF0000) | half;
            case MANA_HIGH -> this.mana = (this.mana & 0xFFFF) | (half << 16);
            case CAPACITY_LOW -> this.capacity = (this.capacity & 0xFFFF0000) | half;
            case CAPACITY_HIGH -> this.capacity = (this.capacity & 0xFFFF) | (half << 16);
            default -> {
            }
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull final EntityPlayer player) {
        return !this.tile.isInvalid() && player.getDistanceSq(this.tile.getPos().add(0.5, 0.5, 0.5)) <= 64;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
        final Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = slot.getStack();
        final ItemStack original = stack.copy();
        final boolean moved = index < TileManaBuffer.SLOTS
                ? this.mergeItemStack(stack, TileManaBuffer.SLOTS, this.inventorySlots.size(), true)
                : this.mergeItemStack(stack, 0, TileManaBuffer.SLOTS, false);
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return original;
    }
}
