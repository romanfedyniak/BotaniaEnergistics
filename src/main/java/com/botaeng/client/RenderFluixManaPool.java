package com.botaeng.client;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.handler.MultiblockRenderHandler;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.common.block.tile.mana.TilePool;

import com.botaeng.tile.TileFluixManaPool;

/**
 * Botania's pool renderer, which draws only over Botania's own block. The surface stands as high as a mana pool
 * holding the same mana, so a network with a pool's worth or more looks full.
 */
public class RenderFluixManaPool extends TileEntitySpecialRenderer<TileFluixManaPool> {

    @Override
    public void render(@Nonnull final TileFluixManaPool pool, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage, final float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableRescaleNormal();
        final float a = MultiblockRenderHandler.rendering ? 0.6F : 1.0F;
        GlStateManager.color(1, 1, 1, a);
        GlStateManager.translate(x, y, z);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        final float waterLevel = (float) Math.min(pool.getCurrentMana(), TilePool.MAX_MANA) / TilePool.MAX_MANA * 0.4F;
        final float v = 0.125F;
        final float w = -v * 3.5F;

        final Block below = pool.getWorld().getBlockState(pool.getPos().down()).getBlock();
        if (below instanceof IPoolOverlayProvider) {
            final TextureAtlasSprite overlay = ((IPoolOverlayProvider) below).getIcon(pool.getWorld(), pool.getPos());
            if (overlay != null) {
                final float s = 0.0625F;
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.color(1, 1, 1,
                        a * (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 20.0) + 1) * 0.3 + 0.2));
                GlStateManager.translate(-0.5F, -1.43F, -0.5F);
                GlStateManager.rotate(90, 1, 0, 0);
                GlStateManager.scale(s, s, s);
                renderIcon(overlay);
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
            }
        }

        if (waterLevel > 0) {
            final float s = 0.0546875F;
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, a);
            GlStateManager.translate(w, -1.0F - (0.43F - waterLevel), w);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.scale(s, s, s);
            ShaderHelper.useShader(ShaderHelper.manaPool);
            renderIcon(MiscellaneousIcons.INSTANCE.manaWater);
            ShaderHelper.releaseShader();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }

        // Botania leaves these as it set them; put back what the next renderer expects.
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /** A full-bright 16×16 square of the sprite. */
    private static void renderIcon(final TextureAtlasSprite icon) {
        final Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, ClientProxy.POSITION_TEX_LMAP);
        tessellator.getBuffer().pos(0, 16, 0).tex(icon.getMinU(), icon.getMaxV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(16, 16, 0).tex(icon.getMaxU(), icon.getMaxV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(16, 0, 0).tex(icon.getMaxU(), icon.getMinV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(0, 0, 0).tex(icon.getMinU(), icon.getMinV()).lightmap(240, 240).endVertex();
        tessellator.draw();
    }
}
