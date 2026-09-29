package com.botaeng.tile;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

import com.botaeng.BotaEngConfig;

/**
 * A chest's worth of items and a store of mana that do nothing by themselves: an interface fills it with a
 * recipe, and whatever the player builds around it takes the recipe out. To Botania it is a pool, so a spreader
 * beside it draws its mana as from any other, a spark on top takes it away, and the network's buses read it as
 * one. Mana goes in only with a pattern: bursts, sparks and buses find it full.
 */
public class TileManaBuffer extends TileEntity implements IManaPool, ISparkAttachable {

    public static final int SLOTS = 27;

    /** How many heights the surface in the block steps through; the client hears of a change only between them. */
    private static final int LEVELS = 64;

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(final int slot) {
            TileManaBuffer.this.markDirty();
        }
    };

    private int mana;
    private int sentLevel = -1;

    public ItemStackHandler getItems() {
        return this.items;
    }

    public static int getCapacity() {
        return Math.max(1, BotaEngConfig.manaBufferCapacity);
    }

    /** How much more it takes. */
    public int getSpace() {
        return Math.max(0, getCapacity() - this.mana);
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public boolean isFull() {
        return this.mana >= getCapacity();
    }

    /** Only draws: whatever offers mana here is refused, since it goes in only with a pattern. */
    @Override
    public void recieveMana(final int mana) {
        if (mana < 0) {
            this.change(mana);
        }
    }

    /** What an ME interface pushes with a pattern: the one way mana goes in. Answers how much fits. */
    public int fill(final int mana, final boolean simulate) {
        final int taken = Math.min(Math.max(0, mana), this.getSpace());
        if (!simulate && taken > 0) {
            this.change(taken);
        }
        return taken;
    }

    private void change(final int mana) {
        final int old = this.mana;
        this.mana = (int) Math.max(0, Math.min((long) this.mana + mana, getCapacity()));
        if (old != this.mana) {
            this.markDirty();
            this.sendLevel();
        }
    }

    /** How full it looks: 0 when empty, then 1 to {@link #LEVELS}. */
    private int level() {
        return this.mana <= 0 ? 0 : 1 + (int) ((long) this.mana * (LEVELS - 1) / getCapacity());
    }

    private void sendLevel() {
        if (!(this.world instanceof WorldServer) || this.level() == this.sentLevel) {
            return;
        }
        this.sentLevel = this.level();
        final PlayerChunkMapEntry watchers = ((WorldServer) this.world).getPlayerChunkMap()
                .getEntry(this.pos.getX() >> 4, this.pos.getZ() >> 4);
        if (watchers != null) {
            watchers.sendPacket(this.getUpdatePacket());
        }
    }

    /** How full the pool inside is drawn, from 0 to 1. */
    public float getFill() {
        return Math.min(1.0F, (float) this.mana / getCapacity());
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return false;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    /** A pool can be dyed; a buffer cannot. */
    @Override
    public EnumDyeColor getColor() {
        return EnumDyeColor.WHITE;
    }

    @Override
    public void setColor(final EnumDyeColor color) {
    }

    @Override
    public boolean canAttachSpark(final ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(final ISparkEntity entity) {
    }

    /** The spark standing on top, as a pool finds its own. */
    @Nullable
    @Override
    public ISparkEntity getAttachedSpark() {
        final List<Entity> sparks = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.pos.up()),
                entity -> entity instanceof ISparkEntity);
        return sparks.size() == 1 ? (ISparkEntity) sparks.get(0) : null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.items)
                : super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("items", this.items.serializeNBT());
        tag.setInteger("mana", this.mana);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items.deserializeNBT(tag.getCompoundTag("items"));
        this.mana = tag.getInteger("mana");
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        final NBTTagCompound tag = super.getUpdateTag();
        tag.setInteger("mana", this.mana);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull final NBTTagCompound tag) {
        this.mana = tag.getInteger("mana");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity packet) {
        this.handleUpdateTag(packet.getNbtCompound());
    }
}
