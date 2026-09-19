package com.botaeng.block;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.tile.mana.TilePool;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;

import com.botaeng.lexicon.BotaEngLexicon;
import com.botaeng.tile.TileFluixManaPool;

/**
 * The block of {@link TileFluixManaPool}, shaped and handled like Botania's mana pool.
 */
public class BlockFluixManaPool extends Block implements IWandHUD, IWandable, ILexiconable {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
    private static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0, 0, 0, 1, 1 / 16.0, 1);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0, 0, 15 / 16.0, 1, 0.5, 1);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1 / 16.0);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0, 0, 1 / 16.0, 0.5, 1);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(15 / 16.0, 0, 0, 1, 0.5, 1);

    /** A cable joined on that side, which the model meets with a connector. */
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");

    public BlockFluixManaPool() {
        super(Material.ROCK);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false)
                .withProperty(WEST, false).withProperty(EAST, false));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return state.withProperty(NORTH, hasCable(world, pos, EnumFacing.NORTH))
                .withProperty(SOUTH, hasCable(world, pos, EnumFacing.SOUTH))
                .withProperty(WEST, hasCable(world, pos, EnumFacing.WEST))
                .withProperty(EAST, hasCable(world, pos, EnumFacing.EAST));
    }

    /** Asks the cable itself, since only it knows on the client whether it reaches this far. */
    private static boolean hasCable(final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        final BlockPos at = pos.offset(side);
        // The chunk cache of a render thread must not be made to create a tile entity.
        final TileEntity tile = world instanceof ChunkCache
                ? ((ChunkCache) world).getTileEntity(at, Chunk.EnumCreateEntityType.CHECK)
                : world.getTileEntity(at);
        if (!(tile instanceof IPartHost)) {
            return false;
        }
        final IPart center = ((IPartHost) tile).getPart(AEPartLocation.INTERNAL);
        return center instanceof IPartCable && ((IPartCable) center).isConnected(side.getOpposite());
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new TileFluixManaPool();
    }

    @Override
    public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFluixManaPool && placer instanceof EntityPlayer) {
            ((TileFluixManaPool) tile).getProxy().setOwner((EntityPlayer) placer);
        }
    }

    /** Items thrown in are infused, or charged and emptied, as in any pool. */
    @Override
    public void onEntityCollision(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        final TileEntity tile = world.getTileEntity(pos);
        if (entity instanceof EntityItem && tile instanceof TilePool
                && ((TilePool) tile).collideEntityItem((EntityItem) entity)) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
        }
    }

    @Override
    public boolean onUsedByWand(final EntityPlayer player, final ItemStack stack, final World world,
            final BlockPos pos, final EnumFacing side) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePool) {
            ((TilePool) tile).onWanded(player, stack);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(final Minecraft mc, final ScaledResolution res, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePool) {
            ((TilePool) tile).renderHUD(mc, res);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TilePool)) {
            return 0;
        }
        final TilePool pool = (TilePool) tile;
        return TilePool.calculateComparatorLevel(pool.getCurrentMana(), Math.max(1, pool.manaCap));
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return AABB;
    }

    @Override
    public void addCollisionBoxToList(final IBlockState state, @Nonnull final World world, @Nonnull final BlockPos pos,
            @Nonnull final AxisAlignedBB entityBox, @Nonnull final List<AxisAlignedBB> boxes,
            @Nullable final Entity entity, final boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, boxes, BOTTOM_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, NORTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, EAST_AABB);
    }

    @Override
    public boolean isSideSolid(final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos,
            final EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(final IBlockAccess world, final IBlockState state, final BlockPos pos,
            final EnumFacing side) {
        return side == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    public LexiconEntry getEntry(final World world, final BlockPos pos, final EntityPlayer player,
            final ItemStack lexicon) {
        return BotaEngLexicon.fluixPool;
    }
}
