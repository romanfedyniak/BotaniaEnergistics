package com.botaeng.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import appeng.util.Platform;

/**
 * A sneak-click with a wrench (AE2's network tool among them) takes a block that is not one of AE2's apart as AE2UD
 * takes its own: the block and what it held go into the player's inventory, whatever does not fit at their feet.
 */
final class WrenchDismantling {
    private static final ThreadLocal<EntityPlayer> PLAYER = new ThreadLocal<>();

    private WrenchDismantling() {
    }

    /** @return whether this click is a wrench dismantling the block, done on the server */
    static boolean tryDismantle(final Block block, final World world, final BlockPos pos, final IBlockState state,
                                       final EntityPlayer player, final EnumHand hand) {
        if (!player.isSneaking() || !Platform.isWrench(player, player.getHeldItem(hand), pos)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        final NonNullList<ItemStack> drops = NonNullList.create();
        block.getDrops(drops, world, pos, state, 0);
        PLAYER.set(player);
        try {
            world.setBlockToAir(pos);
        } finally {
            PLAYER.remove();
        }
        Platform.givePlayerDrops(player, drops);
        return true;
    }

    /** What a broken block held: to the dismantling player, or on the ground when it was broken any other way. */
    static void drop(final World world, final BlockPos pos, final List<ItemStack> drops) {
        final EntityPlayer player = PLAYER.get();
        if (player != null) {
            Platform.givePlayerDrops(player, drops);
            return;
        }
        for (final ItemStack stack : drops) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }
}
