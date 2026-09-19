package com.botaeng.part;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import vazkii.botania.common.entity.EntityManaBurst;

import appeng.api.parts.IPartModel;
import appeng.api.parts.P2PTunnelModels;
import appeng.me.GridAccessException;
import appeng.parts.p2p.PartP2PTunnel;

import com.botaeng.BotaEng;

/**
 * A P2P tunnel for mana bursts. A burst that strikes the input's face comes out of an output's face whole,
 * carrying on with its lens, colour, mana and speed in the direction that face looks. With several outputs the
 * bursts take turns; none is split, so nothing a lens does is multiplied.
 * <p>
 * The burst that comes out still answers to the spreader that fired it, so the spreader waits for it as it
 * would for one that flew straight.
 */
public class PartP2PManaBurst extends PartP2PTunnel<PartP2PManaBurst> {

    public static final P2PTunnelModels MODELS = new P2PTunnelModels(BotaEng.id("part/p2p_tunnel_mana"));

    /** Far enough past the face that the burst starts in the next block and never strikes this one. */
    private static final double CLEARANCE = 0.01;

    private int nextOutput;

    public PartP2PManaBurst(final ItemStack is) {
        super(is);
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    /**
     * @return whether a burst striking this tunnel's face would come out somewhere.
     */
    public boolean acceptsBursts() {
        return !this.isOutput() && this.isActive() && !this.activeOutputs().isEmpty();
    }

    public void sendOn(final EntityManaBurst burst) {
        final List<PartP2PManaBurst> outputs = this.activeOutputs();
        if (outputs.isEmpty()) {
            return;
        }
        this.nextOutput = (this.nextOutput + 1) % outputs.size();
        outputs.get(this.nextOutput).emit(burst);
    }

    private List<PartP2PManaBurst> activeOutputs() {
        final List<PartP2PManaBurst> outputs = new ArrayList<>();
        try {
            for (final PartP2PManaBurst output : this.getOutputs()) {
                if (output.isActive()) {
                    outputs.add(output);
                }
            }
        } catch (final GridAccessException ignored) {
        }
        return outputs;
    }

    private void emit(final EntityManaBurst burst) {
        final TileEntity tile = this.getTile();
        final World world = tile.getWorld();
        final EnumFacing face = this.getSide().getFacing();
        final BlockPos pos = tile.getPos();

        final NBTTagCompound tag = new NBTTagCompound();
        burst.writeToNBT(tag);
        final EntityManaBurst copy = new EntityManaBurst(world);
        copy.readFromNBT(tag);
        copy.setUniqueId(UUID.randomUUID());
        copy.dimension = world.provider.getDimension();

        final double speed = Math.sqrt(burst.motionX * burst.motionX + burst.motionY * burst.motionY
                + burst.motionZ * burst.motionZ);
        final double mx = face.getXOffset() * speed;
        final double my = face.getYOffset() * speed;
        final double mz = face.getZOffset() * speed;
        final double reach = 0.5 + CLEARANCE;
        final float yaw = (float) (MathHelper.atan2(mx, mz) * 180 / Math.PI);
        final float pitch = (float) (MathHelper.atan2(my, Math.sqrt(mx * mx + mz * mz)) * 180 / Math.PI);
        copy.setLocationAndAngles(pos.getX() + 0.5 + face.getXOffset() * reach,
                pos.getY() + 0.5 + face.getYOffset() * reach,
                pos.getZ() + 0.5 + face.getZOffset() * reach, yaw, pitch);
        copy.setMotion(mx, my, mz);
        world.spawnEntity(copy);
    }
}
