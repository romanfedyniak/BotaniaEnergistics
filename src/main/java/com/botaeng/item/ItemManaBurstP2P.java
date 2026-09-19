package com.botaeng.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;

import com.botaeng.part.PartP2PManaBurst;

/**
 * The mana burst P2P tunnel as an item, placed on a cable like any other part.
 */
public class ItemManaBurstP2P extends Item implements IPartItem<PartP2PManaBurst> {

    @Nullable
    @Override
    public PartP2PManaBurst createPartFromItemStack(final ItemStack is) {
        return new PartP2PManaBurst(is);
    }

    @Override
    public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos pos,
            final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        return AEApi.instance().partHelper().placeBus(player.getHeldItem(hand), pos, side, player, hand, world);
    }
}
