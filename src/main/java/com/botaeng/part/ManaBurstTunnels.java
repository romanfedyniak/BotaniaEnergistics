package com.botaeng.part;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.common.entity.EntityManaBurst;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.api.util.AEPartLocation;

/**
 * What a cable bus answers Botania with, which knows bursts only by blocks and tiles: a burst striking it, and
 * whether a spreader aimed at it should fire.
 */
public final class ManaBurstTunnels {

    private ManaBurstTunnels() {
    }

    /**
     * A burst that struck the front of a tunnel's input goes on through it; one that struck anything else of the
     * cable bus goes out as against any block.
     */
    public static void onBurstCollision(final IManaBurst burst, final World world, final BlockPos pos) {
        // A spreader traces a fake burst to find what it is aimed at; that one must go nowhere.
        if (world.isRemote || burst.isFake() || !(burst instanceof EntityManaBurst)) {
            return;
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IPartHost)) {
            return;
        }
        final PartP2PManaBurst tunnel = struckTunnel((IPartHost) tile, world, pos, (Entity) burst);
        if (tunnel != null && tunnel.acceptsBursts()) {
            tunnel.sendOn((EntityManaBurst) burst);
        }
    }

    /**
     * Botania names no side, so a cable bus takes bursts while any tunnel on it would.
     */
    public static boolean acceptsBursts(final IPartHost host) {
        for (final AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
            final IPart part = host.getPart(side);
            if (part instanceof PartP2PManaBurst && ((PartP2PManaBurst) part).acceptsBursts()) {
                return true;
            }
        }
        return false;
    }

    /** The tunnel whose front the burst's path meets, from a tick back to where it is heading. */
    @Nullable
    private static PartP2PManaBurst struckTunnel(final IPartHost host, final World world, final BlockPos pos,
            final Entity burst) {
        final Vec3d at = burst.getPositionVector();
        final Vec3d motion = new Vec3d(burst.motionX, burst.motionY, burst.motionZ);
        final RayTraceResult hit = world.getBlockState(pos).collisionRayTrace(world, pos, at.subtract(motion),
                at.add(motion.scale(2)));
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        final SelectedPart selected = host.selectPartGlobal(hit.hitVec);
        return selected.part instanceof PartP2PManaBurst && selected.side.getFacing() == hit.sideHit
                ? (PartP2PManaBurst) selected.part
                : null;
    }
}
