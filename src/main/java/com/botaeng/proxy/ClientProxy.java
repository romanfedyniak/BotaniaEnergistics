package com.botaeng.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import appeng.api.client.AEKeyRendering;

import com.botaeng.client.ManaKeyRenderHandler;
import com.botaeng.me.ManaKeyType;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        AEKeyRendering.register(ManaKeyType.INSTANCE, new ManaKeyRenderHandler());
    }

}
