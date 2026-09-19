package com.botaeng.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import appeng.api.client.AEKeyRendering;

import com.botaeng.BotaEngItems;
import com.botaeng.client.ManaKeyRenderHandler;
import com.botaeng.client.RenderFluixManaPool;
import com.botaeng.me.ManaKeyType;
import com.botaeng.tile.TileFluixManaPool;

public class ClientProxy extends CommonProxy {

    /** Every item's model is named after the item. */
    @SubscribeEvent
    public void onRegisterModels(final ModelRegistryEvent event) {
        BotaEngItems.ITEMS.values().forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        AEKeyRendering.register(ManaKeyType.INSTANCE, new ManaKeyRenderHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluixManaPool.class, new RenderFluixManaPool());
    }

}
