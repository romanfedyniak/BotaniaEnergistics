package com.botaeng.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;

import com.botaeng.BotaEngItems;
import com.botaeng.me.ManaKey;

/**
 * A mana pool that holds nothing itself: what it shows is the mana of the ME network it is on, and what Botania
 * puts in or takes out goes to and from that network. Offline, or without a channel, it reads empty and full.
 * <p>
 * Botania counts a pool's mana in an int, so a network holding more shows two billion.
 */
public class TileFluixManaPool extends TilePool implements IGridProxyable, IActionHost {

    private final AENetworkProxy proxy = new AENetworkProxy(this, "proxy",
            new ItemStack(BotaEngItems.FLUIX_MANA_POOL), true);
    private final IActionSource source = new MachineSource(this);

    /** The server's readings, sent along to the client. */
    private int shownMana;
    private int shownCapacity;

    public TileFluixManaPool() {
        this.proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        // TilePool reads its capacity from Botania's own pool block when this is -1.
        this.manaCap = 0;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (!this.proxy.isReady()) {
                this.proxy.onReady();
            }
            // Refreshed before Botania's tick uses it to charge the items thrown in.
            final int current = this.getCurrentMana();
            this.manaCap = clamp((long) current + this.space());
            if (current != this.shownMana) {
                this.shownMana = current;
                this.markDispatchable();
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
        super.update();
    }

    @Override
    public int getCurrentMana() {
        if (this.world == null || this.world.isRemote) {
            return this.shownMana;
        }
        final MEStorage storage = this.storage();
        return storage == null ? 0 : clamp(this.cachedMana());
    }

    @Override
    public void recieveMana(final int mana) {
        final MEStorage storage = this.storage();
        if (mana == 0 || storage == null) {
            return;
        }
        try {
            // Like a pool, whatever does not fit is lost.
            if (mana > 0) {
                Platform.poweredInsert(this.proxy.getEnergy(), storage, ManaKey.INSTANCE, mana, this.source);
            } else {
                Platform.poweredExtraction(this.proxy.getEnergy(), storage, ManaKey.INSTANCE, -(long) mana, this.source);
            }
        } catch (final GridAccessException e) {
        }
    }

    @Override
    public boolean isFull() {
        return this.space() <= 0;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return this.space();
    }

    /** How much more the network would take right now. */
    private int space() {
        final MEStorage storage = this.storage();
        return storage == null ? 0
                : clamp(storage.insert(ManaKey.INSTANCE, Integer.MAX_VALUE, Actionable.SIMULATE, this.source));
    }

    private long cachedMana() {
        try {
            return this.proxy.getStorage().getCachedInventory().get(ManaKey.INSTANCE);
        } catch (final GridAccessException e) {
            return 0;
        }
    }

    @Nullable
    private MEStorage storage() {
        if (this.world == null || this.world.isRemote || !this.proxy.isActive()) {
            return null;
        }
        try {
            return this.proxy.getStorage().getInventory();
        } catch (final GridAccessException e) {
            return null;
        }
    }

    private static int clamp(final long mana) {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, mana));
    }

    @Override
    public void writePacketNBT(final NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setInteger("mana", this.getCurrentMana());
    }

    @Override
    public void readPacketNBT(final NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.shownMana = cmp.getInteger("mana");
        if (this.world != null && this.world.isRemote) {
            this.shownCapacity = this.manaCap;
            // Botania's pool gives off particles by how full it is; this one does as a pool holding the same mana.
            this.manaCap = Math.max(TilePool.MAX_MANA, this.shownMana);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.proxy.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.proxy.readFromNBT(tag);
    }

    @Override
    public boolean shouldRefresh(final World world, final BlockPos pos, @Nonnull final IBlockState oldState,
            @Nonnull final IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.proxy.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.proxy.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
        this.proxy.validate();
    }

    /** Botania's own draws the pool block's variant, which this block does not have. */
    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(final Minecraft mc, final ScaledResolution res) {
        final ItemStack pool = new ItemStack(BotaEngItems.FLUIX_MANA_POOL);
        HUDHandler.drawSimpleManaHUD(0x4444FF, this.getCurrentMana(), Math.max(1, this.shownCapacity),
                pool.getDisplayName(), res);
        final int x = res.getScaledWidth() / 2 - 11;
        final int y = res.getScaledHeight() / 2 + 30;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        mc.renderEngine.bindTexture(HUDHandler.manaBar);
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x, y, 0, this.isOutputtingPower() ? 22 : 0,
                38, 22, 15);
        GlStateManager.color(1, 1, 1, 1);
        final ItemStack tablet = new ItemStack(ModItems.manaTablet);
        ItemManaTablet.setStackCreative(tablet);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(tablet, x - 20, y);
        mc.getRenderItem().renderItemAndEffectIntoGUI(pool, x + 26, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public AENetworkProxy getProxy() {
        return this.proxy;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void gridChanged() {
    }

    @Nullable
    @Override
    public IGridNode getGridNode(@Nonnull final AEPartLocation dir) {
        return this.proxy.getNode();
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull final AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {
        this.world.destroyBlock(this.pos, true);
    }

    @Override
    public IGridNode getActionableNode() {
        return this.proxy.getNode();
    }
}
