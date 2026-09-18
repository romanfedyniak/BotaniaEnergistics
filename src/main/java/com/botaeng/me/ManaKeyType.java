package com.botaeng.me;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import vazkii.botania.common.block.ModBlocks;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AmountFormat;

import com.botaeng.BotaEng;

/**
 * Mana as a kind of content a network can hold, counted one mana at a time. A byte of a cell holds 1000, so a
 * 1k cell is about one pool.
 */
public final class ManaKeyType extends AEKeyType {

    public static final ManaKeyType INSTANCE = new ManaKeyType();

    /** What one mana pool holds. */
    public static final int POOL = 1_000_000;

    private ManaKeyType() {
        super(BotaEng.id("mana"), ManaKey.class, new TextComponentTranslation("botaeng.mana"));
    }

    @Override
    public AEKey readFromPacket(@Nonnull final ByteBuf input) {
        return ManaKey.INSTANCE;
    }

    @Override
    public AEKey loadKeyFromTag(@Nonnull final NBTTagCompound tag) {
        return ManaKey.INSTANCE;
    }

    @Override
    public int getAmountPerOperation() {
        return 1000;
    }

    @Override
    public int getAmountPerByte() {
        return 1000;
    }

    @Override
    public long getDefaultCraftAmount() {
        return 1000;
    }

    @Override
    public String formatAmount(final long amount, final AmountFormat format) {
        final String mana = super.formatAmount(amount, format);
        if (format != AmountFormat.FULL && format != AmountFormat.FULL_BASE || amount < POOL) {
            return mana;
        }
        final String pools = amount < 100L * POOL
                ? String.format("%,.1f", amount / (double) POOL)
                : String.format("%,d", amount / POOL);
        return mana + " (" + I18n.translateToLocalFormatted("botaeng.mana.pools", pools) + ")";
    }

    @Override
    public TextFormatting getDisplayColour() {
        return TextFormatting.AQUA;
    }

    @Override
    public ResourceLocation getButtonTexture() {
        return new ResourceLocation("appliedenergistics2", "textures/guis/states.png");
    }

    @Override
    public ItemStack getButtonIcon() {
        return new ItemStack(ModBlocks.pool);
    }
}
