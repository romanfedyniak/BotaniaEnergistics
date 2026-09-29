package com.botaeng.client;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.core.proxy.ClientProxy;

import com.botaeng.tile.TileManaBuffer;

/**
 * The mana under the buffer's glass lid, as high as the buffer is full.
 */
public class RenderManaBuffer extends TileEntitySpecialRenderer<TileManaBuffer> {

    // In pixels, matching the block model: the inner walls and the floor plate.
    private static final float EDGE = 0.1F;
    private static final float FLOOR = 8.7F;
    private static final float TOP = 14.85F;

    @Override
    public void render(@Nonnull final TileManaBuffer buffer, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage, final float alpha) {
        final float fill = buffer.getFill();
        if (fill <= 0) {
            return;
        }
        final float height = (FLOOR + (TOP - FLOOR) * fill) / 16;
        final float min = EDGE / 16;
        final float max = 1 - min;
        final TextureAtlasSprite icon = MiscellaneousIcons.INSTANCE.manaWater;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableAlpha();
        GlStateManager.color(1, 1, 1, 1);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ShaderHelper.useShader(ShaderHelper.manaPool);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertices = tessellator.getBuffer();
        vertices.begin(7, ClientProxy.POSITION_TEX_LMAP);
        vertex(vertices, icon, min, height, min);
        vertex(vertices, icon, min, height, max);
        vertex(vertices, icon, max, height, max);
        vertex(vertices, icon, max, height, min);
        tessellator.draw();

        ShaderHelper.releaseShader();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void vertex(final BufferBuilder vertices, final TextureAtlasSprite icon, final float x,
            final float y, final float z) {
        vertices.pos(x, y, z).tex(icon.getInterpolatedU(x * 16), icon.getInterpolatedV(z * 16))
                .lightmap(240, 240).endVertex();
    }
}
