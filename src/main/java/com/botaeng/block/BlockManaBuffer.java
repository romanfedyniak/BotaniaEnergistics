package com.botaeng.block;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import com.botaeng.BotaEng;
import com.botaeng.BotaEngGuiHandler;
import com.botaeng.tile.TileManaBuffer;

/**
 * The block of {@link TileManaBuffer}.
 */
public class BlockManaBuffer extends Block {

    public BlockManaBuffer() {
        super(Material.ROCK);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new TileManaBuffer();
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX,
            final float hitY, final float hitZ) {
        if (!world.isRemote) {
            player.openGui(BotaEng.INSTANCE, BotaEngGuiHandler.MANA_BUFFER, world, pos.getX(), pos.getY(),
                    pos.getZ());
        }
        return true;
    }

    /** The items fall out; the mana, as from a broken pool, is gone. */
    @Override
    public void breakBlock(final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaBuffer) {
            final IItemHandler items = ((TileManaBuffer) tile).getItems();
            for (int slot = 0; slot < items.getSlots(); slot++) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(slot));
            }
        }
        super.breakBlock(world, pos, state);
    }
}
