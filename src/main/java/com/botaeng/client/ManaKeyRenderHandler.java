package com.botaeng.client;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import appeng.api.client.AEKeyModelContext;
import appeng.api.client.AEKeyRenderHandler;
import appeng.api.stacks.AEKey;

import com.botaeng.me.ManaKey;

/**
 * Mana is drawn as the surface of a mana pool, a sprite Botania already puts in the block atlas.
 */
@SideOnly(Side.CLIENT)
public class ManaKeyRenderHandler implements AEKeyRenderHandler {

    private static final ResourceLocation MANA_WATER = new ResourceLocation("botania", "blocks/mana_water");

    @Nullable
    @Override
    public IBakedModel getModel(final AEKey what, final AEKeyModelContext context) {
        return ManaKey.is(what) ? context.getSpriteModel(MANA_WATER) : null;
    }
}
