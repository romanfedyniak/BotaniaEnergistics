package com.botaeng.me;

import java.util.List;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import appeng.api.stacks.AEKey;

/**
 * Mana, as a network names it. There is only one: mana has no kinds and carries nothing.
 */
public final class ManaKey extends AEKey {

    public static final ManaKey INSTANCE = new ManaKey();

    private static final ResourceLocation ID = new ResourceLocation("botania", "mana");

    private ManaKey() {
    }

    public static boolean is(final AEKey what) {
        return what == INSTANCE;
    }

    @Override
    public ManaKeyType getType() {
        return ManaKeyType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Object getPrimaryKey() {
        return ID;
    }

    @Override
    public ManaKey dropSecondary() {
        return this;
    }

    @Override
    public void toTag(final NBTTagCompound tag) {
    }

    @Override
    public void writeToPacket(final ByteBuf data) {
    }

    @Override
    protected ITextComponent computeDisplayName() {
        return new TextComponentTranslation("botaeng.mana");
    }

    @Override
    public String getModId() {
        return "botania";
    }

    @Override
    public void addDrops(final long amount, final List<ItemStack> drops, @Nonnull final World world,
            @Nonnull final BlockPos pos) {
        // Mana has no item form, so a machine that breaks loses what it held, as a pool does.
    }

    @Override
    public boolean equals(final Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public String toString() {
        return "mana";
    }
}
