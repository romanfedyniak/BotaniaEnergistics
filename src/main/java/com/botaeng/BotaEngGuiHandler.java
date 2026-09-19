package com.botaeng;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.botaeng.client.GuiManaBuffer;
import com.botaeng.container.ContainerManaBuffer;
import com.botaeng.tile.TileManaBuffer;

/**
 * The mod's one window so far, the mana buffer's.
 */
public class BotaEngGuiHandler implements IGuiHandler {

    public static final int MANA_BUFFER = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x,
            final int y, final int z) {
        final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return id == MANA_BUFFER && tile instanceof TileManaBuffer
                ? new ContainerManaBuffer(player.inventory, (TileManaBuffer) tile)
                : null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x,
            final int y, final int z) {
        final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return id == MANA_BUFFER && tile instanceof TileManaBuffer
                ? new GuiManaBuffer(player.inventory, (TileManaBuffer) tile)
                : null;
    }
}
